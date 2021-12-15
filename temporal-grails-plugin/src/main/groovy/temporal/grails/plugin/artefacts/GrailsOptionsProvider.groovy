package temporal.grails.plugin.artefacts

import io.temporal.client.WorkflowOptions
import org.springframework.stereotype.Component

@Component
class GrailsOptionsProvider {

    @Delegate
    private WorkflowOptions.Builder defaultOptionsBuilder
    private WorkflowOptions defaultOptions

    GrailsOptionsProvider() {
    }

    WorkflowOptions getOptions() {
        if (!defaultOptions) {
            defaultOptions = defaultOptionsBuilder.build()
        }

        return defaultOptions
    }

}
