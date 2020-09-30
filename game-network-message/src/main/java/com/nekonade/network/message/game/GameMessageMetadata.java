package com.nekonade.network.message.game;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameMessageMetadata {
    int messageId(); // 消息请求Id
    String desc() default "没有添加消息描述";
    int serviceId(); // 服务Id,消息请求的服务Id。
    EnumMesasageType messageType();//消息类型，request和response
}
