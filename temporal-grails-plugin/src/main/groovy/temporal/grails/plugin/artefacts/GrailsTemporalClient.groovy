package temporal.grails.plugin.artefacts

import groovy.transform.CompileStatic
import groovy.util.logging.Commons
import io.temporal.client.WorkflowClient
import io.temporal.serviceclient.WorkflowServiceStubs
import org.springframework.stereotype.Service

@Service
@Commons
@CompileStatic
class GrailsTemporalClient {

    @Delegate
    private WorkflowClient workflowClient
    private WorkflowServiceStubs workflowServiceStubs

    GrailsTemporalClient() {
        workflowServiceStubs = WorkflowServiceStubs.newInstance()
        workflowClient = newInstance(workflowServiceStubs)
    }

}
