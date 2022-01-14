package temporal.plugin

class WorkflowsRepository {

    private final Map<Class<?>, ElementDescriptor> implementationByDescriptor = new HashMap<>()
    private final Map<Class<?>, ElementDescriptor> interfaceByDescriptor = new HashMap<>()

    void addDescriptor(Class<?> activityInterface, Class<?> theImplementation,
                         List<Class<?>> activities) {
        ElementDescriptor descriptor = new ElementDescriptor(
                theImplementation: theImplementation,
                theInterface: activityInterface,
                activities: activities
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
        List<Class> activities
    }

}
