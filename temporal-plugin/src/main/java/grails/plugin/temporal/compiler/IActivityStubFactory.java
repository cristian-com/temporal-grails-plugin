package grails.plugin.temporal.compiler;

public interface IActivityStubFactory {

    <T> T getActivityInstance(Class<T> activityClass);

}
