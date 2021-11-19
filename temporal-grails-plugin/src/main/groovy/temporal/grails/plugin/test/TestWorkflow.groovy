package temporal.grails.plugin.test

import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface TestWorkflow {

    @WorkflowMethod
    String helloWorld(String arg1)

    void anotherMethod()

}
