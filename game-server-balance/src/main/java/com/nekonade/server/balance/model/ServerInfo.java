package com.nekonade.server.balance.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerInfo {

    private int serviceId; //服务id，与GameMessageMetadata中的一致

    private int serverId;  //服务器id

    private String host;

    private int port;

    private int weight;

}
