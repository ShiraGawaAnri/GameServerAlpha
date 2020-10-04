package com.nekonade.dao.daos;

import com.alibaba.fastjson.JSON;
import com.nekonade.dao.redis.EnumRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;

public abstract class AbstractDao<Entity, ID> {
    
    private static final String RedisDefaultValue = "#null#";

    @Autowired
    protected StringRedisTemplate redisTemplate;

    protected abstract EnumRedisKey getRedisKey();

    protected abstract MongoRepository<Entity, ID> getMongoRepository();

    protected abstract Class<Entity> getEntityClass();

    public Optional<Entity> findById(ID id) {
        String key = this.getRedisKey().getKey(id.toString());
        String value = redisTemplate.opsForValue().get(key);
        Entity entity = null;
        if (value == null) {// 说明redis中没有用户信息
            key = key.intern();//保证字符串在常量池中
            synchronized (key) {// 这里对openId加锁，防止并发操作，导致缓存击穿。
                value = redisTemplate.opsForValue().get(key);// 这里二次获取一下
                if (value == null) {//如果redis中，还是没有值，再从数据库取
                    Optional<Entity> op = this.getMongoRepository().findById(id);
                    if (op.isPresent()) {// 如果数据库中不为空，存储到redis中。
                        entity = op.get();
                        this.updateRedis(entity, id);
                    } else {
                        this.setRedisDefaultValue(key);//设置默认值，防止缓存穿透
                    }
                } else if(value.equals(RedisDefaultValue)) {
                    value = null;//如果取出来的是默认值，还是返回空
                }
            }
        } else if(value.equals(RedisDefaultValue)){//如果是默认值，也返回空，表示不存在。
            value = null;
        }
        if (value != null) {
            entity = JSON.parseObject(value, this.getEntityClass());
        }
        return Optional.ofNullable(entity);
    }
    private void setRedisDefaultValue(String key) {
        Duration duration = Duration.ofMinutes(1);
        redisTemplate.opsForValue().set(key, RedisDefaultValue,duration);
    }


    private void updateRedis(Entity entity, ID id) {
        String key = this.getRedisKey().getKey(id.toString());
        String value = JSON.toJSONString(entity);
        Duration duration = this.getRedisKey().getTimeout();
        if (duration != null) {
            redisTemplate.opsForValue().set(key, value, duration);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    public void saveOrUpdate(Entity entity, ID id) {
        this.updateRedis(entity, id);
        this.getMongoRepository().save(entity);
    }
    
    public void saveOrUpdateToDB(Entity entity) {
        this.getMongoRepository().save(entity);
    }
    public void saveOrUpdateToRedis(Entity entity,ID id) {
        this.updateRedis(entity, id);
    }
}
