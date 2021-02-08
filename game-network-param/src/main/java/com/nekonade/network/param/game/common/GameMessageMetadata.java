package com.nekonade.network.param.game.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameMessageMetadata {
    int messageId(); // 消息请求Id

    int serviceId(); // 服务Id,消息请求的服务Id。

    EnumMesasageType messageType();//消息类型，request和response
}
