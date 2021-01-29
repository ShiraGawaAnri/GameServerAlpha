package com.nekonade.dao.redis;

import org.springframework.util.StringUtils;

import java.time.Duration;

public enum EnumRedisKey {
    USER_ID_INCR(null), // UserId 自增key
    SESSION_ID_INCR(null),
    USER_ACCOUNT(Duration.ofDays(7)), // 用户信息
    USER_NAME_REGISTER(Duration.ofDays(7)),//已被注册的用户名
    PLAYER_ID_INCR(null),
    PLAYER_NICKNAME(Duration.ofSeconds(30)),
    PLAYERID_TO_PLAYER_NICKNAME(Duration.ofDays(7)),
    PLAYER_INFO(Duration.ofDays(7)),
    SETTINGS_GLOBAL(null),
    ARENA(Duration.ofDays(7)),

    ;
    private final Duration timeout;// 此key的value的expire时间,如果为null，表示value永远不过期

    EnumRedisKey(Duration timeout) {
        this.timeout = timeout;
    }

    public String getKey(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return this.name() + "_" + id;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public String getKey() {
        return this.name();
    }
    
    public static void main(String[] args) {
        
    }

}
