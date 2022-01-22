package temporal.plugin

import spock.lang.Specification

class ExamlesControllerSpec extends Specification  {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        given:
        def script = '''
                import  another.one.ActivityStub
            
                class AnotherClass {
                    static INSTANCE = new AnotherClass()
                    
                    Object resolve(Class aClazz) {
                        return ""
                    }
                }
            
                class Subject {
                    @ActivityStub
                    String theField
                    
                    def hello() {
                        println "hello world ${theField} !!!!!!!!"
                    }
                }
                def c = new Subject()
                c.hello()
            '''
        def result = groovy.test.GroovyAssert.assertScript( script )

        expect:
        groovy.test.GroovyAssert.assertScript( script )
    }
}
