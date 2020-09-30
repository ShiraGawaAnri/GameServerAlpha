package com.nekonade.center.messages.request;


import com.nekonade.network.message.web.AbstractHttpRequestParam;

public class SelectGameGatewayParam extends AbstractHttpRequestParam {
  
    private long playerId; // 角色id


    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    @Override
    protected void haveError() {
       
    }
}
