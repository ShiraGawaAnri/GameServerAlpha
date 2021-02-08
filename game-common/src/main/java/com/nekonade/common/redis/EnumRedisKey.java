package com.nekonade.common.redis;

import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Arrays;

public enum EnumRedisKey {
    SEQUENCE(null),//默认共用自增key
    USER_ID_INCR(null), // UserId 自增key
    SESSION_ID_INCR(null),
    USER_ACCOUNT(Duration.ofDays(7)), // 用户信息
    USER_NAME_REGISTER(Duration.ofDays(7)),//已被注册的用户名
    PLAYER_ID_INCR(null),// PlayerId 自增Key
    MAIL_ID_INCR(null),
    PLAYER_NICKNAME(Duration.ofSeconds(30)),
    PLAYERID_TO_NICKNAME(Duration.ofDays(7)),
    PLAYER_INFO(Duration.ofDays(7)),
    CONFIGS_GLOBAL(null),
    ARENA(Duration.ofDays(7)),
    ITEMSDB(null),
    RAIDBATTLEDB(null),
    //RAIDBATTLE_LIMIT_STAGEIDS(null),
    RAIDBATTLE_LIMIT_COUNTER(null),
    RAIDBATTLE_STAGEID_PLAYERID_TO_RAIDID(Duration.ofMinutes(90)),
    RAIDBATTLE_RAIDID_DETAILS(Duration.ofMinutes(90)),
    RAIDBATTLE_RAIDID_TO_SERVERID(Duration.ofMinutes(90)),
    RAIDBATTLE_SAMETIME_RAID_LIMIT(null),
    ;
    private final Duration timeout;// 此key的value的expire时间,如果为null，表示value永远不过期

    EnumRedisKey(Duration timeout) {
        this.timeout = timeout;
    }

    public static void main(String[] args) {

    }

    public String getKey(String id) {
        if (StringUtils.isEmpty(id)) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return this.name() + "_" + id;
    }

    public String getKey(String... ids) {
        if (ids.length == 0) {
            throw new IllegalArgumentException("参数不能为空");
        }
        return this.name() + "_" + String.join("_", Arrays.asList(ids));
    }

    public Duration getTimeout() {
        return timeout;
    }

    public String getKey() {
        return this.name();
    }

}
