package temporal.grails.plugin.artefacts

import grails.compiler.traits.TraitInjector
import groovy.transform.CompileStatic

@CompileStatic
class ActivityTraitInjector implements TraitInjector {

    @Override
    Class getTrait() {
        return GrailsActivity
    }

    @Override
    String[] getArtefactTypes() {
        return ['Activity'] as String[]
    }

}
