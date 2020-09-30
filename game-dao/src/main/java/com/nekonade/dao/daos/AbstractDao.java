package com.nekonade.dao.daos;

import com.alibaba.fastjson.JSON;
import com.nekonade.dao.redis.IRedisKeyConfig;
import com.nekonade.dao.redis.RedisCacheTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;

public abstract class AbstractDao<Entity, ID> {
    
    @Autowired
    protected StringRedisTemplate redisTemplate;
    @Autowired
    protected RedisCacheTemplate redisCacheTemplate;

    protected abstract IRedisKeyConfig getRedisKey();

    protected abstract MongoRepository<Entity, ID> getMongoRepository();

    protected abstract Class<Entity> getEntityClass();
    
    
    /**
     * 
     * <p>Description:先从redis缓存中获取，缓存中没有从数据库获取，获取成功之后会自动缓存到redis中 </p>
     * @param entityId
     * @return
     * @author wang guang shuai 
     * @date  2020年1月10日 下午6:46:00
     *
     */
    public Optional<Entity> findByIdFromCacheOrLoader(ID entityId) {
        String param = entityId.toString();
        String key = this.getRedisKey().getKey(param);
        String value = redisCacheTemplate.getValue(key, param, this.getRedisKey().getExpire(), id->{
            Entity obj = this.getMongoRepository().findById(entityId).orElse(null);
            return obj == null ? null : JSON.toJSONString(obj);
        });
        Entity entity = null;
        if (value != null) {
            entity = JSON.parseObject(value, this.getEntityClass());
        }
        return Optional.ofNullable(entity);
    }
    private void updateRedis(Entity entity, ID id) {
        String key = this.getRedisKey().getKey(id.toString());
        String value = JSON.toJSONString(entity);
        Duration duration = this.getRedisKey().getExpire();
        if (duration != null) {
            redisTemplate.opsForValue().set(key, value, duration);
        } else {
            redisTemplate.opsForValue().set(key, value);
        }
    }
    /**
     * 
     * <p>Description:将对象同时更新到redis和数据库中 </p>
     * @param entity
     * @param id
     * @author wang guang shuai 
     * @date  2020年1月10日 上午10:57:04
     *
     */
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
