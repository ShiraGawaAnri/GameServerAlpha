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

    private double maxPermits = 200;

    private long warmUpPeriodSeconds = 60;

    private double maxWaitingRequests = 2000;

    private long fakeSeconds = 1800;

    private long checkDelaySeconds = 600;
}
