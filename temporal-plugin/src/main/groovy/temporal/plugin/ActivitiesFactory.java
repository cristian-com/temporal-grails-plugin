package temporal.plugin;

import io.temporal.workflow.Workflow;
import org.springframework.stereotype.Component;

/**
 * Factory methods must use the wrapper when the first parameter is a Class.
 * Otherwise, it will conflict with ambiguous methods in the Spring Bean Factory
 **/
@Component
public class ActivitiesFactory {

    GrailsTemporalClient grailsTemporalClient;

    public static <T> T getActivityInstance(Class<T> clazz) {
        return Workflow.newActivityStub(clazz);
    }

}
