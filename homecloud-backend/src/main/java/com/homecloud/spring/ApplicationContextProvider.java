package com.homecloud.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("applicationContextProvider")
public class ApplicationContextProvider implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationContextProvider.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        try {
            return getApplicationContext().getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            logger.error("get bean exception", e);
            return null;
        }
    }
}