package com.tower.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @Author: 梦-屿-千-寻
 * @Date: 2021/2/4 17:36
 * @Version 1.0
 */
@Service
public class MyApplicationContextUti implements ApplicationContextAware {
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext context)
            throws BeansException {
        MyApplicationContextUti.context = context;
    }
    public static ApplicationContext getContext() {
        return context;
    }
    public static  <T> T  getBean(Class<T> tClass){
        return MyApplicationContextUti.getContext().getBean(tClass);
    }
    public static  <T> T  getBean(String name,Class<T> tClass){
        return MyApplicationContextUti.getContext().getBean(name,tClass);
    }
}
