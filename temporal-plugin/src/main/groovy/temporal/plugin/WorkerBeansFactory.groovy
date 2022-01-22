package temporal.plugin

import groovy.util.logging.Commons
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.stereotype.Component

import java.lang.reflect.Field
import java.lang.reflect.Modifier

@Commons
@Component
class WorkerBeansFactory implements BeanDefinitionRegistryPostProcessor {

    @Autowired
    GrailsTemporalClient grailsTemporalClient
    ConfigurableBeanFactory springBeanFactory

    WorkflowContext workflowContext = new WorkflowContext()

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        springBeanFactory = beanFactory
        def e = springBeanFactory.getBean(ActivityEx)
    }

    @Override
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Map<Class<?>, Set<BeanDefinitionWrapper>> activities = BeanImplementationsScanner.scanActivities(registry)
        registerActivities(activities, registry)

        Map<Class<?>, Set<BeanDefinitionWrapper>> workflows = BeanImplementationsScanner.scanWorkflows(registry)
        registerWorkflows(workflows)
    }

    private void registerWorkflows(Map<Class<?>, Set<BeanDefinitionWrapper>> workflowImplementations) {
        workflowImplementations.each { activityInterface, beanDefinitions ->
            beanDefinitions.each { beanDefinition ->
                Map<String, Object> dependencies = new HashMap<>()
                Map<String, Class<?>> activities = new HashMap<>()

                beanDefinition.resolveBeanClass().getDeclaredFields().each { Field field ->
                    Class<?> activity = workflowContext.activityExists(field.type)

                    if (activity) {
                        if (field.isAnnotationPresent(Qualifier) || field.isAnnotationPresent(Autowired)) {
                            log.error("Remove @Qualifier or @Autowired annotations from property: ${field.name}, " +
                                    "class: ${workflowContext.getActivityImplementation(activity)}. \n" +
                                    "This is better to be avoided, the workflow will be loaded automatically to Grails. ")
                            throw new IllegalArgumentException("Activities can not be annotated with @Autowired or " +
                                    "@Qualifier in workflows.")
                        }

                        activities.put(field.name, activity)
                        return
                    }

                    if (field.isAnnotationPresent(Qualifier)) {
                        String qualifier = field.getAnnotation(Qualifier)
                        def bean = springBeanFactory.getBean(qualifier)
                        if (!bean) {
                            throw new IllegalStateException("Not bean found with name ${qualifier} in class" +
                                    " ${workflowContext.getActivityImplementation(activity)}.")
                        }

                        dependencies.put(field.name, qualifier)
                    }

                    if (field.isAnnotationPresent(Autowired)) {
                        def bean = springBeanFactory.getBean(field.type)
                        if (!bean) {
                            throw new IllegalStateException("Not bean found with name ${qualifier} in class" +
                                    " ${workflowContext.getActivityImplementation(activity)}.")
                        }

                        dependencies.put(field.name, field.type)
                    }
                }

                workflowContext.addWorkflow(activityInterface, beanDefinition.resolveBeanClass(),
                        activities, dependencies)
            }
        }
    }

    private void createWorkers() {
        activitiesRepository.getWorkflows().each { workflowInterface, beanDefinitions ->
            grailsTemporalClient.createWorker("queue", workflowInterface, beanDefinitions.each { it.resolveBeanClass() })
        }
    }

    private void registerActivities(Map<Class<?>, Set<BeanDefinitionWrapper>> activities,
                                    BeanDefinitionRegistry registry) {
        activities.each { activityInterface, beanDefinitions ->
            beanDefinitions.each { beanDefinition ->
                String beanName = workflowContext.addActivity(activityInterface, beanDefinition.resolveBeanClass())
                configureActivityBeanDefinition(beanDefinition, registry)

                registry.registerBeanDefinition(beanName, beanDefinition)
            }
        }
    }

    private static void configureActivityBeanDefinition(BeanDefinitionWrapper beanDefinitionWrapper,
                                                        BeanDefinitionRegistry registry) {
        Map<String, RuntimeBeanReference> dependencies = getActivityDependencies(beanDefinitionWrapper.resolveBeanClass(),
                registry)

        dependencies.each { String dependencyName, RuntimeBeanReference beanReference ->
            beanDefinitionWrapper.getPropertyValues().addPropertyValue(dependencyName, beanReference)
        }

        beanDefinitionWrapper.setLazyInit(false)
    }

    private static Map<String, RuntimeBeanReference> getActivityDependencies(Class<?> target,
                                                                             BeanDefinitionRegistry beansRegistry) {
        Map<String, RuntimeBeanReference> dependencies = new HashMap<>()

        target.getDeclaredFields().each { Field field ->
            if (Modifier.isNative(field.getModifiers())) {
                return
            }

            getRuntimeReferenceFromField(beansRegistry, field).ifPresent{ RuntimeBeanReference beanRef ->
                dependencies.put(field.name, beanRef)
            }
        }

        return dependencies
    }

    private static Optional<RuntimeBeanReference> getRuntimeReferenceFromField(BeanDefinitionRegistry beansRegistry,
                                                                               Field field) {
        String name = null
        InjectionType type = null

        if (field.isAnnotationPresent(Qualifier)) {
            String qualifier = field.getAnnotation(Qualifier).value()
            if (beansRegistry.containsBeanDefinition(qualifier)) {
                name = qualifier
                type = InjectionType.NAME
            }
        } else if (field.isAnnotationPresent(Autowired)) {
            if (beansRegistry.containsBeanDefinition(field.getName())) {
                name = field.name
                type = InjectionType.TYPE
            } else if (beansRegistry.containsBeanDefinition(field.type.simpleName)) {
                name = field.type.simpleName
            }
        }

        if (type) {
            return Optional.of(new RuntimeBeanReference(field.type))
        } else if (name) {
            return Optional.of(new RuntimeBeanReference(name))
        }

        return Optional.empty()
    }

    private enum InjectionType {
        NAME, TYPE
    }

}
