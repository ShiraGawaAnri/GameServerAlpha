package com.nekonade.dao.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Function;

/**
 * 
 * @ClassName: RedisCacheTemplate
 * @Description: 这是一个redis缓存的模板。在redis做为缓存的时候，需要防止缓存的雪崩，穿透
 * @author: wang guang shuai
 * @date: 2020年1月9日 下午5:11:53
 */
@Service
public class RedisCacheTemplate {
    private static final String DefaultRedisNullValue = "#-#";
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 
     * <p>
     * Description:第一次会从redis中获取，如果redis中没有此值，从db中获取
     * </p>
     * 
     * @param redisKey
     * @param param
     * @param duration
     * @param selectFromDB
     * @return
     * @author wang guang shuai
     * @date 2020年1月9日 下午8:07:52
     *
     */
    public String getValue(String redisKey, String param, Duration duration, Function<String, String> selectFromDB) {
        String value = redisTemplate.opsForValue().get(redisKey);
        if (value == null) {
            // 加锁，防止缓存穿透和击穿
            synchronized (redisKey.intern()) {
                // 二次检测
                value = redisTemplate.opsForValue().get(redisKey);
                if (value == null) {// 如果等于空，从数据库取
                    value = selectFromDB.apply(param);
                    if (value == null) {// 如果数据库还是没有，说明是真的没有，添加空标记
                        value = DefaultRedisNullValue;
                    }
                    // 将取到的值缓存到redis中。
                    if (duration != null) {
                        redisTemplate.opsForValue().set(redisKey, value, duration);
                    } else {
                        redisTemplate.opsForValue().set(redisKey, value);
                    }
                }
            }
        }
        if (value.equals(DefaultRedisNullValue)) {
            return null;
        }
        return value;
    }
    


}
