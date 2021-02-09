package com.nekonade.raidbattle.message.rpc;


import com.nekonade.network.param.game.common.IGameMessage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RaidBattleRPCEvent {

    Class<? extends IGameMessage> value();
}
