package com.nekonade.dao.redis;

import java.time.Duration;

public interface IRedisKeyConfig {

     String getKey(String id);
     String getKey();

     Duration getExpire();


}
