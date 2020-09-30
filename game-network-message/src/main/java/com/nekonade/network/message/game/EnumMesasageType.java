package com.nekonade.network.message.game;

public enum EnumMesasageType {
    REQUEST(1),        //客户端请求消息
    RESPONSE(2),       //客户端响应消息
    PUSH(3),           //服务器主动推送的消息
    RPC_REQUEST(4),    //RPC请求消息
    RPC_RESPONSE(5)    //RPC响应消息
    ;
    private final int type;
    EnumMesasageType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }
    public static EnumMesasageType getType(int type) {
    	for(EnumMesasageType messageType : EnumMesasageType.values()) {
    		if(messageType.getType() == type) {
    			return messageType;
    		}
    	}
    	return null;
    }
}
