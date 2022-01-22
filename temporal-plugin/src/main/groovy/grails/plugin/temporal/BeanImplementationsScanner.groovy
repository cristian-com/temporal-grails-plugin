package grails.plugin.temporal

import io.temporal.activity.ActivityInterface
import io.temporal.workflow.WorkflowInterface
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner
import org.springframework.core.type.filter.AssignableTypeFilter

class BeanImplementationsScanner {

    static final String BASE_PACKAGE = "temporal.plugin"

    static Reflections reflections = new Reflections(BASE_PACKAGE)

    static Map<Class<?>, Set<BeanDefinitionWrapper>> scanActivities(BeanDefinitionRegistry registry) {
        return scanSubclassesOfAnnotatedWith(registry, ActivityInterface.class)
    }

    static Map<Class<?>, Set<BeanDefinitionWrapper>> scanWorkflows(BeanDefinitionRegistry registry) {
        return scanSubclassesOfAnnotatedWith(registry, WorkflowInterface.class)
    }

    static Map<Class<?>, Set<BeanDefinitionWrapper>> scanSubclassesOfAnnotatedWith(BeanDefinitionRegistry registry,
                                                                                   Class<?> annotation) {
        Set<Class<?>> interfaces = getAllClassesAnnotatedWith(annotation)
        Map<Class<?>, Set<BeanDefinitionWrapper>> beansByAnnotation = new HashMap<>()

        interfaces.each { Class<?> type ->
            ClassPathBeanDefinitionScanner pathScanner = new ClassPathBeanDefinitionScanner(registry, false)
            if (type.isInterface()) {
                pathScanner.addIncludeFilter(new AssignableTypeFilter(type))
            }

            beansByAnnotation.put(type, pathScanner
                    .findCandidateComponents(BASE_PACKAGE)
                    .collect { BeanDefinitionWrapper.wrap(it) }
                    .toSet())
        }

        return beansByAnnotation
    }

    private static Set<Class<?>> getAllClassesAnnotatedWith(Class<?> annotation) {
        return reflections.get(Scanners.TypesAnnotated.with(annotation).asClass())
    }

}
