package temporal.plugin

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller

@Controller
class Example {

    def world() {
        render "Hello World!"
    }

}
