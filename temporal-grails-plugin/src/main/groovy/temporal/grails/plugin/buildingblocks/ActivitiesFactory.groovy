package temporal.grails.plugin.buildingblocks
/**
 * Factory methods must use the wrapper when the first parameter is a Class.
 * Otherwise it will conflict with ambiguous methods in the Spring Bean Factory
 **/
class ActivitiesFactory {

    // resources.groovy
    static final String FACTORY_NAME = ActivitiesFactory.name
    static final String METHOD_FACTORY_NAME = 'getActivityInstance'

    def <T> T getActivityInstance(BeanArgsWrapper args) {
        assert !args.isEmpty()
        Class<T> clazz = args[0] as Class<T>
        return clazz.newInstance()
    }

}
