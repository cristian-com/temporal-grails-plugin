package temporal.grails.plugin.buildingblocks

import static java.util.Objects.requireNonNull

class WorkflowRepository {

    private final Map<ImplementationType, Set<BeanDefinitionWrapper>> implementations = new HashMap<>()

    void addActivity(BeanDefinitionWrapper beanDefinitionWrapper) {
        requireNonNull(beanDefinitionWrapper)

        implementations.get(ImplementationType.ACTIVITY).add(beanDefinitionWrapper)
    }

    void addWorkflow(BeanDefinitionWrapper beanDefinitionWrapper) {
        requireNonNull(beanDefinitionWrapper)

        implementations.get(ImplementationType.WORKFLOW).add(beanDefinitionWrapper)
    }

    Set<BeanDefinitionWrapper> getActivities() {
        return implementations.get(ImplementationType.ACTIVITY)
    }

    Set<BeanDefinitionWrapper> getWorkflows() {
        return implementations.get(ImplementationType.WORKFLOW)
    }

    enum ImplementationType {
        ACTIVITY, WORKFLOW
    }

}
