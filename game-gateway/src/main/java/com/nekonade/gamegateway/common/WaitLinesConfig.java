package com.nekonade.gamegateway.common;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "game.gateway.waitlines.config")
@Getter
@Setter
public class WaitLinesConfig {

    private Double maxPermits;

    private Long warmUpPeriodSeconds;

    private Double maxWaitingRequests;

    private Long fakeSeconds;

    private Long checkDelaySeconds;
}
