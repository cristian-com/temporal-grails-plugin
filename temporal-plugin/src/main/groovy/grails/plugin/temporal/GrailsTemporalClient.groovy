package grails.plugin.temporal

import groovy.transform.CompileStatic
import groovy.util.logging.Commons
import io.temporal.client.WorkflowClient
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.worker.Worker
import io.temporal.worker.WorkerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
@Commons
@CompileStatic
class GrailsTemporalClient {

    @Delegate
    private WorkflowClient workflowClient
    private WorkflowServiceStubs workflowServiceStubs
    WorkerFactory workerFactory

    GrailsTemporalClient() {
        workflowServiceStubs = WorkflowServiceStubs.newInstance()
        workflowClient = newInstance(workflowServiceStubs)
        workerFactory = WorkerFactory.newInstance(workflowClient)
    }

    @EventListener(ApplicationReadyEvent)
    void startClient() {
        workerFactory.start()
    }

    void createWorker(String taskQueue, Class workerImplementation, Object activities) {
        Worker worker = workerFactory.newWorker(taskQueue)
        worker.registerWorkflowImplementationTypes(workerImplementation)

        activities.each { worker.registerActivitiesImplementations(it) }
        worker.registerActivitiesImplementations(activities)
    }

}
