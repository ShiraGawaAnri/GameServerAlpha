package com.xinyue.network.message.handler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.xinyue.network.message.game.IGameMessage;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameRequestMapping {

    public Class<? extends IGameMessage> value();
}
