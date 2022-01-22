package grails.plugin.temporal

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Qualifier

@CompileStatic
class ActivitiesRepository {

    private final Map<Class<?>, ElementDescriptor> implementationByDescriptor = new HashMap<>()
    private final Map<Class<?>, ElementDescriptor> interfaceByDescriptor = new HashMap<>()
    private final Map<String, ElementDescriptor> beanNameByDescriptor = new HashMap<>()

    String addDescriptor(Class<?> activityInterface, Class<?> theImplementation) {
        Qualifier qualifier = activityInterface.getAnnotation(Qualifier)
        String beanName = activityInterface.simpleName

        if (qualifier) {
            beanName = qualifier.value()
        }

        ElementDescriptor descriptor = new ElementDescriptor(
                theImplementation: theImplementation,
                theInterface: activityInterface,
                beanName: beanName
        )

        interfaceByDescriptor.put(activityInterface, descriptor)
        implementationByDescriptor.put(activityInterface, descriptor)
        beanNameByDescriptor.put(descriptor.beanName, descriptor)

        return beanName
    }

    Class<?> findImplementationByInterface(Class<?> activity) {
        return interfaceByDescriptor.get(activity).theImplementation
    }

    Class<?> findInterfaceByImpl(Class<?> activity) {
        return implementationByDescriptor.get(activity).theInterface
    }

    static class ElementDescriptor {
        Class theInterface
        Class theImplementation
        String beanName
    }

}
