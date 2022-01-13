package temporal.plugin

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.util.ClassUtils

class BeanDefinitionWrapper {

    @Delegate
    final BeanDefinition beanDefinition
    private Class<?> theClass
    private RuntimeBeanReference runtimeBeanReference

    private BeanDefinitionWrapper(BeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition
    }

    static BeanDefinitionWrapper wrap(BeanDefinition beanDefinition) {
        return new BeanDefinitionWrapper(beanDefinition)
    }

    Class<?> resolveBeanClass() {
        if (!theClass) {
            theClass = ClassUtils.resolveClassName(beanDefinition.getBeanClassName(),null)
        }

        return theClass
    }

}
