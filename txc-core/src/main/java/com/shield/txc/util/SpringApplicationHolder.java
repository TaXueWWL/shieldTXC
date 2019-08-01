package com.shield.txc.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author snowalker
 * @version 1.0
 * @date 2019/8/1 17:19
 * @className SpringApplicationHolder
 * @desc
 */
public class SpringApplicationHolder implements ApplicationContextAware {

    private static ApplicationContext context = null;

    public synchronized static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
