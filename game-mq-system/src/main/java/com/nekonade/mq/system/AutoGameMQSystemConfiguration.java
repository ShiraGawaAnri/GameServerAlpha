package com.nekonade.mq.system;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import com.nekonade.mq.system.mq.GameMQTemplate;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AutoGameMQSystemConfiguration {

	@Autowired
	private RocketMQTemplate rocketMQTemplate;
	
	@Bean
	public GameMQTemplate gameMQTemplate() {
		return new GameMQTemplate(rocketMQTemplate);
	}
}
