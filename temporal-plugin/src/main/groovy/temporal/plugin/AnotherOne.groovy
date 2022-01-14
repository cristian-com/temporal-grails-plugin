package temporal.plugin

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

import javax.inject.Provider

@Service
class AnotherOne {

    Provider<ActivityEx> hello

    @EventListener(ApplicationReadyEvent)
    void hello() {
        print("HELLO")
    }

}
