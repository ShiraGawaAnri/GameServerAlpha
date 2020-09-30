package com.nekonade.center.logicconfig;

import com.nekonade.dao.redis.IRedisKeyConfig;
import org.springframework.util.StringUtils;

import java.time.Duration;

public enum GameCenterRedisKeyConifg implements IRedisKeyConfig {
    USER_ID_INCR(null), // UserId 自增key
    PLAYER_ID_INCR(null),
    PLAYER_NICKNAME(null),
    ;
    private final Duration expire;// 此key的value的expire时间,如果为null，表示value永远不过期

    GameCenterRedisKeyConifg(Duration expire) {
        this.expire = expire;
    }

    @Override
    public String getKey(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return this.name() + "_" + id;
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
