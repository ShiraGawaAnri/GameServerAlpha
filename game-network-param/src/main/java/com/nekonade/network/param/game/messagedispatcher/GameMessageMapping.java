package com.nekonade.network.param.game.messagedispatcher;


import com.nekonade.network.param.game.common.IGameMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameMessageMapping {

    Class<? extends IGameMessage> value();
}
