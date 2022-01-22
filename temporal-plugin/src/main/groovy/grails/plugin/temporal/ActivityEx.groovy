package grails.plugin.temporal

import io.temporal.activity.ActivityInterface
import io.temporal.workflow.WorkflowMethod

@ActivityInterface
interface ActivityEx {

    @WorkflowMethod
    void methodOne()

}