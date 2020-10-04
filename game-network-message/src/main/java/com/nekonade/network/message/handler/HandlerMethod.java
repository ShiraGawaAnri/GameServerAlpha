package com.xinyue.network.message.handler;

import java.lang.reflect.Method;

public class HandlerMethod {

    private Object targetObj;//处理消息的目标对象
    private Method targetMethod;//处理消息的目标方法
    
    public HandlerMethod(Object targetObj, Method targetMethod) {
        super();
        this.targetObj = targetObj;
        this.targetMethod = targetMethod;
    }
    public Object getTargetObj() {
        return targetObj;
    }
    public Method getTargetMethod() {
        return targetMethod;
    }
    
    
}
