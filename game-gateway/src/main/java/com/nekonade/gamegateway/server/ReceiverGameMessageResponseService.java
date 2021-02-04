package com.nekonade.gamegateway.server;

import com.nekonade.gamegateway.common.GatewayServerConfig;
import com.nekonade.network.param.game.bus.GameMessageInnerDecoder;
import com.nekonade.network.param.game.common.GameMessagePackage;
import io.netty.channel.Channel;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * 
 * @ClassName: GameMessageConsume 
 * @Description: 接收业务服务返回的消息，并发送到客户端
 * @author: wgs
 * @date: 2019年5月15日 上午9:17:48
 */
@Service
public class ReceiverGameMessageResponseService {
	private static final Logger logger = LoggerFactory.getLogger(ReceiverGameMessageResponseService.class);
    @Autowired
    private GatewayServerConfig gatewayServerConfig;
    @Autowired
    private ChannelService channelService;
    @PostConstruct
    public void init() {
        logger.info("监听消息接收业务消息topic:{}",gatewayServerConfig.getGatewayGameMessageTopic());
    }

    @KafkaListener(topics = {"${game.gateway.server.config.gateway-game-message-topic}"}, groupId = "${game.gateway.server.config.server-id}")
    public void receiver(ConsumerRecord<String, byte[]> record) {
        GameMessagePackage gameMessagePackage = GameMessageInnerDecoder.readGameMessagePackage(record.value());
        Long playerId = gameMessagePackage.getHeader().getPlayerId();//从包头中获取这个消息包归属的playerId
        Channel channel = channelService.getChannel(playerId);//根据playerId找到这个客户端的连接Channel
        if(channel != null) {
            channel.writeAndFlush(gameMessagePackage);//给客户端返回消息
        }
    }
}
