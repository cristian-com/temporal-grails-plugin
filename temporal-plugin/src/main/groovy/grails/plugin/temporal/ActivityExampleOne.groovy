package grails.plugin.temporal

import org.springframework.beans.factory.annotation.Autowired

class ActivityExampleOne implements ActivityEx {

    @Autowired
    GrailsTemporalClient grailsTemporalClient

    @Override
    void methodOne() {
        def x = grailsTemporalClient
        print(x)
    }

}
