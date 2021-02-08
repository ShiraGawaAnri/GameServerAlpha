package com.nekonade.common.eventsystem;

import java.lang.reflect.Method;

public class GameEventListenerMapping {
    private final Object bean;//处理事件方法所在的bean类
    private final Method method;//处理事件的方法

    public GameEventListenerMapping(Object bean, Method method) {
        super();
        this.bean = bean;
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }
}
