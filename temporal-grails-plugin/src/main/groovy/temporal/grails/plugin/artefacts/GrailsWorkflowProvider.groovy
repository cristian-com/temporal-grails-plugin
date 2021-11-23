package temporal.grails.plugin.artefacts

import groovy.transform.CompileStatic
import io.temporal.client.WorkflowClient
import io.temporal.workflow.Functions
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder
import temporal.grails.plugin.test.moneytransferapp.InitiateMoneyTransfer

import java.lang.reflect.Method

class GrailsWorkflowProvider {

    private Reflections reflections
    private Map<Class<?>, List<Method>> workflowMapping = new HashMap()
    private Closure workflowMethod
    private String workflowMethodName
    private Class theInterface

    private GrailsWorkflowProvider(Class<?> theClass) {
        reflections = new Reflections(
                new ConfigurationBuilder()
                        .addScanners(Scanners.MethodsAnnotated)
                        .addScanners(Scanners.SubTypes)
                        .forPackage("temporal.grails.plugin.test"))

        resolveWorkflowInterface(theClass)

        if (!theInterface) {
            return
        }

        generateWorkflowMapping()
        resolveWorkflowMethod()
    }

    static <T> T getArtefact(Class<T> theClass) {
        def builder = new GrailsWorkflowProvider(theClass)
        return builder.getProxy()
    }

    private <T> T getProxy(boolean temporalProxy = false) {
        if (temporalProxy) {
            return WorkflowClient.newWorkflowStub(theInterface, "" as String) as T
        }

        T proxy = {}.asType(theInterface) as T

        proxy.metaClass.getProperty = { name -> throw new IllegalAccessException() }
        proxy.metaClass.setProperty = { name -> throw new IllegalAccessException() }
        proxy.metaClass.invokeMethod = { String name, Object args ->
            if (workflowMethodName == name) {
                return workflowMethod(args)
            } else {
                throw new IllegalAccessException()
            }
        }

        return proxy
    }

    @CompileStatic
    private <T> T getTemporalProxy(Class<T> clazz) {
        return (WorkflowClient.newInstance(null)).newWorkflowStub(theInterface, "") as T
    }

    private void generateWorkflowMapping() {
        workflowMapping.put(WorkflowMethod, getMainMethod())
    }

    private List<Method> getMainMethod() {
        Set<Method> workflowMethods = reflections.get(Scanners.MethodsAnnotated.with(WorkflowMethod).as(Method))

        if (!workflowMethods) {
            // This is miss-configuration, here will fail but probably later make it configurable
            throw new IllegalStateException("The workflow has more than one method annotated with 'WorkflowMethod'")
        }

        return workflowMethods.toList()
    }

    private void resolveWorkflowInterface(Class<?> theClass) {
        if (theClass.isInterface()) {
            WorkflowInterface annotation = theClass.getAnnotation(WorkflowInterface)

            if (annotation) {
                this.theInterface = theClass
            }
        } else {
            Class<?>[] interfaces = theClass.getInterfaces()
            Class<?> theWorkflowInterface

            for (iterator in 0..<interfaces.size()) {
                theWorkflowInterface = interfaces[iterator]
                WorkflowInterface annotation = theWorkflowInterface.getAnnotation(WorkflowInterface)

                if (annotation) {
                    this.theInterface = theClass
                }
            }
        }
    }

    private void resolveWorkflowMethod() {
        Method workflowMetaMethod = (workflowMapping.get(WorkflowMethod) as List<Method>).first()
        boolean isFunction = workflowMetaMethod.returnType != void

        workflowMethodName = workflowMetaMethod.getName()

        switch (workflowMetaMethod.parameterCount) {
            case 1:
                fourArgMethod(isFunction)
                break
            case 4:
                fourArgMethod(isFunction)
                break
        }
    }

    private void oneArgMethod(boolean function) {
        if (function) {
            this.workflowMethod = { arg0 ->
                return start(theInterface.&workflowMethodName as Functions.Func1<?, ?>, arg0)
            }
        } else {
            this.workflowMethod = { arg0 ->
                return start(theInterface.&workflowMethodName as Functions.Proc1<?, ?>, arg0)
            }
        }
    }

    private void fourArgMethod(boolean function) {
        if (function) {
            this.workflowMethod = { arg0 ->
                return WorkflowClient.start(theInterface.&workflowMethodName as Functions.Func1<?, ?, ?, ?, ?>, arg0)
            }
        } else {
            this.workflowMethod = { arg0 ->
                def stub = InitiateMoneyTransfer.getClient().newWorkflowStub(theInterface, InitiateMoneyTransfer.getOptions())
                def method = stub.&"${workflowMethodName}" as Functions.Proc4<?, ?, ?, ?>
                return WorkflowClient.start(method, arg0[0], arg0[1], arg0[2], arg0[3])
            }
        }
    }

}
