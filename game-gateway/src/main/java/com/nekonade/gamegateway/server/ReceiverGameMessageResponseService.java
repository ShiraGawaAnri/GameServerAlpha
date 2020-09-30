package com.nekonade.gamegateway.server;

import com.nekonade.gamegateway.server.handler.codec.GateResponseMessage;
import com.nekonade.network.message.game.GameInnerMessage;
import com.nekonade.network.message.game.GameMessageHeader;
import com.nekonade.network.message.game.GameMessageInnerCodec;
import io.netty.channel.Channel;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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
@RocketMQMessageListener(topic = "${game.gateway.config.gateway-game-message-topic}-${game.gateway.config.server-id}",
consumerGroup = "${spring.cloud.nacos.discovery.namespace}-game-logic-message-consumer-group-${game.gateway.config.server-id}")
public class ReceiverGameMessageResponseService implements RocketMQListener<MessageExt> {
	private static final Logger logger = LoggerFactory.getLogger(ReceiverGameMessageResponseService.class);
	@Autowired
	private ChannelService channelService;
	@Autowired
	private Environment environment;
	@PostConstruct
	public void init() {
		RocketMQMessageListener rocketMQMessageListener = this.getClass().getAnnotation(RocketMQMessageListener.class);
		if(rocketMQMessageListener == null) {
			logger.error("没有添加消费消息监听");
			return ;
		}
		logger.info("监听消息接收业务消息topic:{},group:{}",environment.resolveRequiredPlaceholders(rocketMQMessageListener.topic()),environment.resolveRequiredPlaceholders(rocketMQMessageListener.consumerGroup()));
	}

	@Override
	public void onMessage(MessageExt message) {
	        sendMessage(message, channelService);
	}
	
	public static void sendMessage(MessageExt message, ChannelService channelService) {
	    byte[] data = message.getBody();
        if (data != null) {
            try {
                GameInnerMessage gameInnerMessage = GameMessageInnerCodec.readGameMessagePackage(data);
                GameMessageHeader header = gameInnerMessage.getHeader();
                long playerId = header.getPlayerId();
                Channel channel = channelService.getChannel(playerId);// 根据playerId找到这个客户端的连接Channel
                if (channel != null) {
                    GateResponseMessage responseMessage = new GateResponseMessage(gameInnerMessage.getHeader(),
                            gameInnerMessage.getBody());
                    channel.writeAndFlush(responseMessage);// 给客户端返回消息
                }
                logger.debug("网关返回消息：{}",header);
            } catch (Throwable e) {
                logger.error("网关接收消息异常", e);
            }
        }
	}
}
