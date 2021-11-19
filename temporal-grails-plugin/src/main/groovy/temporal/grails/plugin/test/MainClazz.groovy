package temporal.grails.plugin.test

import io.temporal.api.common.v1.WorkflowExecution
import temporal.grails.plugin.artefacts.GrailsWorkflowProvider;

class MainClazz {

    static void main(String[] args) {
        TestWorkflow testWorkflow = GrailsWorkflowProvider.getArtefact(TestWorkflow)
        WorkflowExecution result = testWorkflow.helloWorld("Hello")
        print("Hello")
    }

}
