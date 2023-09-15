package com.spring.security.utils;

//import com.google.common.collect.Lists;
//import com.sumec.itc.basic.util.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author lxl
 * @date 2023/9/13 18:23
 */
@Component
public final class SpringUtils implements BeanFactoryPostProcessor, ApplicationContextAware{

    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    private static ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        SpringUtils.beanFactory = beanFactory;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

//    /**
//     * 根据bean的名字获取对象
//     *
//     * @param name bean的名字
//     * @return Object 一个以所给名字注册的bean的实例
//     * @throws BeansException BeansException
//     */
//    public static <T> T getBean(String name) throws BeansException {
//        return ObjectUtils.cast(beanFactory.getBean(name));
//    }

    /**
     * 根据类型获取对应实例
     *
     * @param clz 类型
     * @return Object 一个以所给类型注册的bean的实例
     * @throws BeansException BeansException
     */
    public static <T> T getBean(Class<T> clz) throws BeansException {
        return beanFactory.getBean(clz);
    }

}
