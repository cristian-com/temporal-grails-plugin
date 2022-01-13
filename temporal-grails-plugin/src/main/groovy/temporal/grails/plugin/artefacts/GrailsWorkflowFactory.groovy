package temporal.grails.plugin.artefacts

import io.temporal.worker.Worker
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class GrailsWorkflowFactory {

    @PostConstruct
    void registerWorkflows() {
        Worker worker = GrailsTemporalClient.new
    }

}
