package com.nekonade.network.message.context;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="game.server.config")
public class ServerConfig {
    /**
     * 游戏服务id
     */
    private int serviceId;
    /**
     * 游戏服务所在的服务器id
     */
    private int serverId;
    private String businessGameMessageTopic = "business-game-message-topic";//业务服务监听消息的topic
    private String gatewayGameMessageTopic = "gateway-game-message-topic";//网关接收消息监听的topic
    private int workerThreads = 4;//业务处理线程数
    private int dbThreads = 16;//db处理线程数
    private int flushRedisDelaySecond = 60;
    private int flushDBDelaySeond = 300;
    
    
   
	public int getFlushRedisDelaySecond() {
        return flushRedisDelaySecond;
    }
    public void setFlushRedisDelaySecond(int flushRedisDelaySecond) {
        this.flushRedisDelaySecond = flushRedisDelaySecond;
    }
    public int getFlushDBDelaySeond() {
        return flushDBDelaySeond;
    }
    public void setFlushDBDelaySeond(int flushDBDelaySeond) {
        this.flushDBDelaySeond = flushDBDelaySeond;
    }
    public int getDbThreads() {
        return dbThreads;
    }
    public void setDbThreads(int dbThreads) {
        this.dbThreads = dbThreads;
    }
    public int getWorkerThreads() {
        return workerThreads;
    }
    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }
    public String getBusinessGameMessageTopic() {
        return businessGameMessageTopic;
    }
    public void setBusinessGameMessageTopic(String businessGameMessageTopic) {
        this.businessGameMessageTopic = businessGameMessageTopic;
    }
    public String getGatewayGameMessageTopic() {
        return gatewayGameMessageTopic;
    }
    public void setGatewayGameMessageTopic(String gatewayGameMessageTopic) {
        this.gatewayGameMessageTopic = gatewayGameMessageTopic;
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
