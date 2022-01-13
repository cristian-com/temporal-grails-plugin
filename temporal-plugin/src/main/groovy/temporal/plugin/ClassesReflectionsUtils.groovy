package temporal.plugin

import groovy.transform.CompileStatic

import java.lang.annotation.Annotation

@CompileStatic
class ClassesReflectionsUtils {

    private ClassesReflectionsUtils() {}

    static Optional<Class> resolveInterfaceAnnotatedWith(Class<? extends Annotation> annotationType,
                                                         Class<?> theClass) {
        if (theClass.isInterface()) {
            Annotation annotation = theClass.getAnnotation(annotationType)

            if (annotation) {
                return Optional.of(theClass)
            }
        } else {
            Class<?>[] interfaces = theClass.getInterfaces()
            Class<?> theWorkflowInterface

            for (index in 0..<interfaces.size()) {
                theWorkflowInterface = interfaces[index]
                Annotation annotation = theWorkflowInterface.getAnnotation(Annotation)

                if (annotation) {
                    return Optional.of(theClass)
                }
            }
        }

        return Optional.empty()
    }

}
