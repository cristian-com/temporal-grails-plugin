package temporal.plugin

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@PackageScope
@CompileStatic
class WorkflowContext {

    ActivitiesRepository activitiesRepository = new ActivitiesRepository()
    WorkflowsRepository workflowsRepository = new WorkflowsRepository()

    void addWorkflow(Class<?> activityInterface, Class<?> theImplementation,
                     List<Class<?>> activities) {
        workflowsRepository.addDescriptor(activityInterface, theImplementation, activities)
    }

    boolean activityExists(Class<?> activity) {
        if (activity.isInterface()) {
            activitiesRepository.interfaceExists(activity)
        } else {
            activitiesRepository.implementationExists(activity)
        }
    }

    String addActivity(Class<?> activityInterface, Class<?> theImplementation) {
        return activitiesRepository.addDescriptor(activityInterface, theImplementation)
    }

}
