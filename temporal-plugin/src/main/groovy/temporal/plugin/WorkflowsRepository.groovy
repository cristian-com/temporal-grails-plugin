package temporal.plugin

class WorkflowsRepository {

    private final Map<Class<?>, ElementDescriptor> implementationByDescriptor = new HashMap<>()
    private final Map<Class<?>, ElementDescriptor> interfaceByDescriptor = new HashMap<>()

    void addDescriptor(Class<?> activityInterface, Class<?> theImplementation,
                       Map<String, Class<?>> activities, Map<String, Object> dependencies) {
        ElementDescriptor descriptor = new ElementDescriptor(
                theImplementation: theImplementation,
                theInterface: activityInterface,
                activities: activities,
                dependencies: dependencies
        )

        interfaceByDescriptor.put(activityInterface, descriptor)
        implementationByDescriptor.put(activityInterface, descriptor)
    }

    boolean interfaceExists(Class<?> activity) {
        return interfaceByDescriptor.containsKey(activity)
    }

    boolean implementationExists(Class<?> activity) {
        return implementationByDescriptor.containsKey(activity)
    }

    static class ElementDescriptor {
        Class theInterface
        Class theImplementation
        Map<String, Object> dependencies
        Map<String, Class<?>> activities
    }

}
