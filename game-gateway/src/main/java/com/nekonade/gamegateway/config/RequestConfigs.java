package com.nekonade.gamegateway.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "request.config")
public class RequestConfigs {

    private List<RequestConfigLimiters> limiters;

    private boolean allServerMaintenance = false;

    private long maintenanceStartTime;

    private long maintenanceEndTime;
}
