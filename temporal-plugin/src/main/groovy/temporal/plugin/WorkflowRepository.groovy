package temporal.plugin

import org.springframework.stereotype.Component

import static java.util.Objects.requireNonNull

class WorkflowRepository {

    private final Map<ImplementationType, Set<ElementDescriptor>> implementations = new HashMap<>()

    WorkflowRepository () {
        ImplementationType.values().each { ImplementationType implType ->
            implementations.put(implType, new HashSet<ElementDescriptor>())
        }
    }

    void addActivity(ElementDescriptor beanDefinitionWrapper) {
        requireNonNull(beanDefinitionWrapper)

        implementations.get(ImplementationType.ACTIVITY).add(beanDefinitionWrapper)
    }

    void addWorkflow(ElementDescriptor beanDefinitionWrapper) {
        requireNonNull(beanDefinitionWrapper)

        implementations.get(ImplementationType.WORKFLOW).add(beanDefinitionWrapper)
    }

    Set<ElementDescriptor> getActivities() {
        return implementations.get(ImplementationType.ACTIVITY)
    }

    Set<ElementDescriptor> getWorkflows() {
        return implementations.get(ImplementationType.WORKFLOW)
    }

    enum ImplementationType {
        ACTIVITY, WORKFLOW
    }

    static class ElementDescriptor {
        Class theInterface
        Class theImplementation
        String beanName
    }

}
