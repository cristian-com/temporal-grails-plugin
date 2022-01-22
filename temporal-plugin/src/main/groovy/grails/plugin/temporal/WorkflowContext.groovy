package grails.plugin.temporal

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@PackageScope
@CompileStatic
class WorkflowContext {

    ActivitiesRepository activitiesRepository = new ActivitiesRepository()
    WorkflowsRepository workflowsRepository = new WorkflowsRepository()

    void addWorkflow(Class<?> activityInterface, Class<?> theImplementation, Map<String, Class<?>> activities,
                     Map<String, Object> dependencies) {
        workflowsRepository.addDescriptor(activityInterface, theImplementation, activities, dependencies)
    }

    Class activityExists(Class<?> activity) {
        if (activity.isInterface()) {
            activitiesRepository.findImplementationByInterface(activity)
            return activity
        } else {
            return activitiesRepository.findInterfaceByImpl(activity)
        }
    }

    Class getActivityImplementation(Class<?> activity) {
        return activitiesRepository.findImplementationByInterface(activity)
    }

    String addActivity(Class<?> activityInterface, Class<?> theImplementation) {
        return activitiesRepository.addDescriptor(activityInterface, theImplementation)
    }

}
