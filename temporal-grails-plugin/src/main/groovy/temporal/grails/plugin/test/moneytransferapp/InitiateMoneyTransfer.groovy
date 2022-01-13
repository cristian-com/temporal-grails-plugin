package temporal.grails.plugin.test.moneytransferapp

import io.temporal.api.common.v1.WorkflowExecution
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import io.temporal.serviceclient.WorkflowServiceStubs
import temporal.grails.plugin.artefacts.GrailsWorkflowProvider

class InitiateMoneyTransfer {

    GrailsWorkflowProvider grailsWorkflowProvider
    static WorkflowClient client
    static WorkflowServiceStubs service
    static WorkflowOptions options

    static void main(String[] args) throws Exception {
        // WorkflowServiceStubs is a gRPC stubs wrapper that talks to the local Docker instance of the Temporal server.
        service = WorkflowServiceStubs.newInstance()
        options = WorkflowOptions.newBuilder()
                .setTaskQueue("")
                // A WorkflowId prevents this it from having duplicate instances, remove it to duplicate.
                //.setWorkflowId("money-transfer-workflow")
                .build()

        // WorkflowClient can be used to start, signal, query, cancel, and terminate Workflows.
        client = WorkflowClient.newInstance(service)

        // WorkflowStubs enable calls to methods as if the Workflow object is local, but actually perform an RPC.
        MoneyTransferWorkflow workflow = GrailsWorkflowProvider.getArtefact(MoneyTransferWorkflow)
        String referenceId = UUID.randomUUID().toString()
        String fromAccount = "001-001"
        String toAccount = "002-002"
        double amount = 18.74

        // Asynchronous execution. This process will exit after making this call.
        WorkflowExecution we = workflow.transfer(fromAccount, toAccount, referenceId, amount)
        System.out.printf("\nTransfer of %f from account %s to account %s is processing\n", amount, fromAccount, toAccount)
        System.out.printf("\nWorkflowID: %s RunID: %s", we.getWorkflowId(), we.getRunId())
        System.exit(0)
    }
    
}

