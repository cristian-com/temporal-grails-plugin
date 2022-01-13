package temporal.plugin

import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor
import org.springframework.stereotype.Component

import java.lang.reflect.Field

@Component
class WorkerBeansFactory implements BeanDefinitionRegistryPostProcessor {

    @Autowired
    GrailsTemporalClient grailsTemporalClient

    WorkflowRepository workflowRepository = new WorkflowRepository()
    ConfigurableBeanFactory springBeanFactory

    @Override
    void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        springBeanFactory = beanFactory
        springBeanFactory.getBean(workflowRepository.getActivities())
    }

    @Override
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Map<Class<?>, Set<BeanDefinitionWrapper>> activities = BeanImplementationsScanner.scanActivities(registry)
        registerActivities(activities, registry)

        Map<Class<?>, Set<BeanDefinitionWrapper>> workflows = BeanImplementationsScanner.scanWorkflows(registry)
    }

    private void registerActivities(Map<Class<?>, Set<BeanDefinitionWrapper>> activities,
                                           BeanDefinitionRegistry registry) {
        activities.each { activityInterface, beanDefinitions ->
            beanDefinitions.each { beanDefinition ->
                Class implementation = beanDefinition.resolveBeanClass()
                Qualifier qualifier = implementation.getAnnotation(Qualifier)
                String beanName = activityInterface.simpleName

                if (qualifier) {
                    beanName = qualifier.value()
                }

                def activityDescriptor = new WorkflowRepository.ElementDescriptor(
                        theImplementation: implementation,
                        theInterface: activityInterface,
                        beanName: beanName
                )

                workflowRepository.addActivity(activityDescriptor)

                registry.registerBeanDefinition(beanName, beanDefinition)
            }
        }
    }

    private void createWorkers(Map<Class, Set<BeanDefinitionWrapper>> workflows, Set<BeanDefinitionWrapper> activities) {
        workflows.each { workflowInterface, beanDefinitions ->
            grailsTemporalClient.createWorker("queue", workflowInterface, beanDefinitions.each { it.resolveBeanClass() })
        }

    }

    private static Set<BeanDefinitionWrapper> registerBeanFactories(Set<BeanDefinitionWrapper> beanDefinitions,
                                                                    BeanDefinitionRegistry registry, String factoryName,
                                                                    String factoryMethod) {
        beanDefinitions.each { BeanDefinitionWrapper beanDefinitionWrapper ->
            String beanName = beanDefinitionWrapper.getBeanClassName()
            BeanDefinition beanDefinition = getConfiguredBeanDefinition(beanDefinitionWrapper, registry,
                    factoryName, factoryMethod)
            registry.registerBeanDefinition(beanName, beanDefinition)
        }
    }

    private static BeanDefinition getConfiguredBeanDefinition(BeanDefinitionWrapper beanDefinitionWrapper,
                                                              BeanDefinitionRegistry registry, String factoryName,
                                                              String factoryMethod) {
        beanDefinitionWrapper.setScope(BeanDefinition.SCOPE_PROTOTYPE)
        beanDefinitionWrapper.setFactoryBeanName(factoryName)
        beanDefinitionWrapper.setFactoryMethodName(factoryMethod)
        beanDefinitionWrapper.setLazyInit(false)

        Map<String, RuntimeBeanReference> dependencies = getDependencies(beanDefinitionWrapper.resolveBeanClass(),
                registry)

        dependencies.each { String dependencyName, RuntimeBeanReference beanReference ->
            beanDefinitionWrapper.getPropertyValues().addPropertyValue(dependencyName, beanReference)
        }

        return beanDefinitionWrapper.beanDefinition
    }

    public

    private static Map<String, RuntimeBeanReference> getDependencies(Class<?> target, BeanDefinitionRegistry beansRegistry) {
        Map<String, RuntimeBeanReference> dependencies = new HashMap<>()

        target.getDeclaredFields().each { Field field ->
            if (beansRegistry.containsBeanDefinition(field.getName())) {
                dependencies.put(field.getName(), new RuntimeBeanReference(field.getName()))
            }
        }

        return dependencies
    }

}
