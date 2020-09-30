package com.nekonade.server.balance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoGameServerBalanceConfiguration {

	@Autowired
	private ApplicationContext context;
	@Bean
	public BusinessServerService businessServerService() {
		return new BusinessServerService();
	}
	@Bean
	public PlayerServiceInstance playerServiceInstance() {
		return new PlayerServiceInstance(context);
	}
}
