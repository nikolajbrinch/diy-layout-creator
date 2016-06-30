package org.diylc.application

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component


@Component
class ApplicationWindowFactory implements ApplicationContextAware {

    ApplicationContext applicationContext

    public DiylcFrame newWindow() {
//        return applicationContext.getAutowireCapableBeanFactory().createBean(DiylcFrame.class)
        return applicationContext.getBean(DiylcFrame.class)
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext
    }
}
