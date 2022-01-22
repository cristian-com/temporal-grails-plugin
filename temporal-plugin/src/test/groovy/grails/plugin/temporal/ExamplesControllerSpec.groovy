package grails.plugin.temporal

import spock.lang.Specification

class ExamplesControllerSpec extends Specification  {

    void "test something"() {
        given:
        def script = '''
                import grails.plugin.temporal.annotations.ActivityStub
                import grails.plugin.temporal.compiler.IActivityStubFactory
                import grails.plugin.temporal.compiler.ActivityStubFactoryBridge
            
                class FakeFactory implements IActivityStubFactory {
                
                    <T> T getActivityInstance(Class<T> activityClass) {
                        Objects.requireNonNull(activityClass)
                        return activityClass.getDeclaredConstructor().newInstance()
                    }
                
                }
                
                class Example {
                    String hello = "Hello World"
                }
                
                ActivityStubFactoryBridge.FACTORY = new FakeFactory()
            
                class Subject {
                    @ActivityStub
                    Example theField
                    
                    def hello() {
                        println "${theField.hello} !!!!!!!!"
                    }
                }
                def c = new Subject()
                c.hello()
            '''
        expect:
        groovy.test.GroovyAssert.assertScript( script )
    }
}
