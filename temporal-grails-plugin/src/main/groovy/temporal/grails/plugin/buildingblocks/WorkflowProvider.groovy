package temporal.grails.plugin.buildingblocks

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

class WorkflowProvider implements ApplicationContextAware {

    private ApplicationContext applicationContext

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
    }

    def <T> T get(Class<T> clazz) {
       return applicationContext.getBean(clazz.getCanonicalName(),
               BeanArgsWrapper.of(clazz)) as T
    }

}
