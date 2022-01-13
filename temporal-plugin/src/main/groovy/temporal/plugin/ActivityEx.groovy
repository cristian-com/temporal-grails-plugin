package temporal.plugin

import io.temporal.activity.ActivityInterface
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@ActivityInterface
interface ActivityEx {

    @WorkflowMethod
    void methodOne()

}