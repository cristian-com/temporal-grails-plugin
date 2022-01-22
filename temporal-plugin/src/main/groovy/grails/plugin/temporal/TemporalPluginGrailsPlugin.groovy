package grails.plugin.temporal

import grails.plugins.Plugin

class TemporalPluginGrailsPlugin extends Plugin {

    def grailsVersion = "5.1.1 > *"

    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]

    def title = "Temporal Grails Plugin"
    def author = "Cristian Morales"
    def authorEmail = "cristiannkb@gmail.com"
    def description = '''\
        Temporal grails integration. This plugin's goal is to provide annotations and services to get 
        the Java Temporal SDK client up and working with a grails application. 
        '''
    def profiles = ['web', 'rest']
    def license = "APACHE"
    def scm = [url: "https://github.com/cristian-com/temporal-grails-plugin/"]

    Closure doWithSpring() {
        { ->
        }
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
