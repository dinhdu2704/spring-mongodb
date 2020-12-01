package vn.vnpt.stc.enterprise.configuration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by huyvv
 * Date: 06/02/2020
 * Time: 5:45 PM
 * for all issues, contact me: huyvv@vnpt-technology.vn
 **/
@Component
public final class SpringContext implements ApplicationContextAware {

    private SpringContext(){

    }
    private static ApplicationContext context;

    public static <T extends Object> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
