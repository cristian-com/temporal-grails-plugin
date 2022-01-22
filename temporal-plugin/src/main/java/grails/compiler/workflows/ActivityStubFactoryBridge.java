package grails.compiler.workflows;

import temporal.plugin.ActivitiesFactory;

public class ActivityStubFactoryBridge {

    static <T> T get(Class<T> activityClass) {
        return ActivitiesFactory.getActivityInstance(activityClass);
    }

}
