package com.nekonade.dao.redis;

import org.springframework.util.StringUtils;

import java.time.Duration;

public enum DaoRedisKeyConifg implements IRedisKeyConfig{

    USER_ACCOUNT(Duration.ofDays(7)), // 用户信息
    PLAYER_INFO(Duration.ofDays(7)),
    USER_NAME_MAPPER_ID(Duration.ofDays(7)),
    ;
    private final Duration expire;// 此key的value的expire时间,如果为null，表示value永远不过期

    DaoRedisKeyConifg(Duration expire) {
        this.expire = expire;
    }
    
    public static String Namespace = "defaultNamespace";

    @Override
    public String getKey(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return Namespace + ":" + this.name() + "_" + id;
    }

    @Override
    public Duration getExpire() {
        return expire;
    }

    @Override
    public String getKey() {
        return this.name();
    }

}
