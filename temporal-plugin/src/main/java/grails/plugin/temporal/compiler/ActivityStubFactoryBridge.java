package grails.plugin.temporal.compiler;

public class ActivityStubFactoryBridge {

    static IActivityStubFactory FACTORY = null;

    static <T> T get(Class<T> activityClass) {
        return FACTORY.getActivityInstance(activityClass);
    }

}
