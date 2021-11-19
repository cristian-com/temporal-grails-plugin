package temporal.grails.plugin.test.impl

import temporal.grails.plugin.test.TestWorkflow

class TestWorkflowImpl implements TestWorkflow {

    @Override
    String helloWorld(String arg1) {
        println "Hello + " + arg1
    }

    @Override
    void anotherMethod() {
        println "Bye"
    }

}
