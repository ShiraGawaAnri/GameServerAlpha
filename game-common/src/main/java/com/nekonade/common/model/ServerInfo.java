package com.nekonade.common.model;

public class ServerInfo {
    private int serviceId; //服务id，与GameMessageMetadata中的一致
    private int serverId;  //服务器id
    private String host;
    private int port;
   

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    
    
    
}
