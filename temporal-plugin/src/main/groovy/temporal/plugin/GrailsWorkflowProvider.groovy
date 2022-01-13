package temporal.plugin

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
    private Map<Class<?>, List<Method>> workflowMapping = new HashMap()
    private Closure workflowMethod
    private String workflowMethodName
    private Class theInterface

    GrailsTemporalClient grailsTemporalClient = null

    private GrailsWorkflowProvider(Class<?> theClass) {
        reflections = new Reflections(
                new ConfigurationBuilder()
                        .addScanners(Scanners.MethodsAnnotated)
                        .forPackage("temporal.grails.plugin.test"))

        resolveWorkflowInterface(theClass)
        generateWorkflowMapping()
        resolveWorkflowMethod()
    }

    static <T> T getArtefact(Class<T> theClass) {
        def builder = new GrailsWorkflowProvider(theClass)
        return builder.getProxy()
    }

    private <T> T getProxy(boolean temporalProxy = false) {
        if (temporalProxy) {
            return grailsTemporalClient.newWorkflowStub(theInterface, "" as String) as T
        }

        T proxy = {}.asType(theInterface) as T

        proxy.metaClass.getProperty = { name -> throw new IllegalAccessException() }
        proxy.metaClass.setProperty = { name -> throw new IllegalAccessException() }
        proxy.metaClass.invokeMethod = { String name, args ->
            if (workflowMethodName == name) {
                return workflowMethod(args as Object[])
            } else {
                throw new IllegalAccessException()
            }
        }

        return proxy
    }

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
        theInterface = ClassesReflectionsUtils.resolveInterfaceAnnotatedWith(WorkflowInterface, theClass)
                .orElseThrow { new IllegalArgumentException("The class is not annotated as WorkflowInterface. Please add ${WorkflowInterface}") }
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
                return start(theInterface.&workflowMethodName as Functions.Proc1<?>, arg0)
            }
        }
    }

    private void fourArgMethod(boolean function) {
        if (function) {
            this.workflowMethod = { Objects[] args ->
                return WorkflowClient.start(theInterface.&(this.workflowMethodName) as Functions.Func1<Object, Object>, args[0])
            }
        } else {
            this.workflowMethod = { Object[] args ->
                def stub = grailsTemporalClient.newWorkflowStub(theInterface, InitiateMoneyTransfer.getOptions())
                def method = stub.&"${workflowMethodName}"
                return WorkflowClient.start(method as Functions.Proc4<Object, Object, Object, Object>,
                        args[0], args[1], args[2], args[3])
            }
        }
    }

}
