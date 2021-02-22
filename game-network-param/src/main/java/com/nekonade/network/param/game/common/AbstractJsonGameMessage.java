package com.nekonade.network.param.game.common;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nekonade.common.utils.JacksonUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;

public abstract class AbstractJsonGameMessage<T> extends AbstractGameMessage {

    private T bodyObj;//具体的参数类实例对象。所有的请求参数和响应参数，必须以对象的形式存在。

    public AbstractJsonGameMessage() {
        if (this.getBodyObjClass() != null) {
            try {
                //bodyObj = this.getBodyObjClass().newInstance();
                bodyObj = this.getBodyObjClass().getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
                bodyObj = null;
            }

        }
    }


    @SneakyThrows
    @Override
    protected byte[] encode() {//使用JSON，将参数对象序列化
        //String str = JSON.toJSONString(bodyObj);
        String str = JacksonUtils.toJsonString(bodyObj);
        return str.getBytes();
    }

    @SneakyThrows
    @Override
    protected void decode(byte[] body) {//使用JSON，将收到的数据反序列化
        String str = new String(body);
        //bodyObj = JSON.parseObject(str, this.getBodyObjClass());
        bodyObj = JacksonUtils.parseObject(str,this.getBodyObjClass());
    }

    @Override
    protected boolean isBodyMsgNull() {
        return this.bodyObj == null;
    }

    protected abstract Class<T> getBodyObjClass();//由子类返回具体的参数对象类型。

    public T getBodyObj() {
        return bodyObj;
    }

    public void setBodyObj(T bodyObj) {
        this.bodyObj = bodyObj;
    }

    @SneakyThrows
    @Override
    public String toString() {//重写toString，方便打印日志
        String msg = null;
        if (this.bodyObj != null) {
            //msg = JSON.toJSONString(bodyObj);
            msg = JacksonUtils.toJsonString(bodyObj);
        }
        return "Header:" + this.getHeader() + ", " + this.getClass().getSimpleName() + "=[bodyObj=" + msg + "]";
    }

    @SneakyThrows
    public String bodyToString() {
        if (this.bodyObj != null) {
            //return JSON.toJSONString(bodyObj);
            return JacksonUtils.toJsonString(bodyObj);
        }
        return null;
    }

}
