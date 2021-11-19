package temporal.grails.plugin.artefacts

import groovy.transform.CompileStatic
import io.temporal.api.common.v1.WorkflowExecution
import io.temporal.client.WorkflowClient
import io.temporal.workflow.Functions
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ConfigurationBuilder

import java.lang.reflect.Method

class GrailsWorkflowProvider {

    private Reflections reflections
    private Class theInterface
    private Map<Class<?>, List<Method>> workflowMapping = new HashMap()
    private Closure workflowMethod
    private String workflowMethodName

    private GrailsWorkflowProvider(Class<?> theClass) {
        reflections = new Reflections(
                new ConfigurationBuilder()
                        .addScanners(Scanners.MethodsAnnotated)
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
    private <T> T getTemporalProxy (Class<T> clazz) {
        return (WorkflowClient.newInstance(null)).newWorkflowStub(theInterface, "") as T
    }

    private void generateWorkflowMapping() {
        workflowMapping.put(WorkflowMethod, getMainMethod())
    }

    private List<Method> getMainMethod() {
        Set<Method> workflowMethods = reflections.get(Scanners.MethodsAnnotated.with(WorkflowMethod).as(Method))

        if (!workflowMethods) {
            // This is miss-configuration, we should fail now and probably later make it configurable
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
        boolean isFunction = workflowMetaMethod.returnType != Void.class

        workflowMethodName = workflowMetaMethod.getName()

        switch (workflowMetaMethod.parameterCount) {
            case 1:
                oneArgMethod(isFunction)
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

    static <A1, R> WorkflowExecution start(Functions.Func1<A1, R> workflow, A1 arg1) {
        return WorkflowExecution.newInstance()
    }

}
