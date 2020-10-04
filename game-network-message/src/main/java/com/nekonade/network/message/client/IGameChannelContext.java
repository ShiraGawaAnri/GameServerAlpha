package com.mygame.game.messagedispatcher;

import com.mygame.game.common.IGameMessage;

public interface IGameChannelContext {
    
    void sendMessage(IGameMessage gameMessage);
    
    <T> T getRequest();
    String getRemoteHost();
    long getPlayerId();

}
