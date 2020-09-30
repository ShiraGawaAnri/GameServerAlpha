package com.nekonade.gamegateway.server;

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
 * @Description: 接收业务服务广播返回的消息，并发送到客户端
 * @author: wgs
 * @date: 2019年5月15日 上午9:17:48
 */
@Service
@RocketMQMessageListener(topic = "${game.gateway.config.gateway-game-message-topic}",
consumerGroup = "${spring.cloud.nacos.discovery.namespace}-game-logic-message-broadcast-consumer-group-${game.gateway.config.server-id}")
public class ReceiverBroadcastGameMessageResponseService implements RocketMQListener<MessageExt> {
	private final Logger logger = LoggerFactory.getLogger(ReceiverBroadcastGameMessageResponseService.class);
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
		ReceiverGameMessageResponseService.sendMessage(message, channelService);
	}
}
