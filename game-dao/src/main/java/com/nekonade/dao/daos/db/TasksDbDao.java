package com.nekonade.dao.daos.db;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.common.utils.JacksonUtils;
import com.nekonade.dao.daos.AbstractDao;
import com.nekonade.dao.db.entity.data.task.TasksDB;
import com.nekonade.dao.db.repository.TasksDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@CacheConfig(cacheManager = "caffeineCacheManager")
@Service
public class TasksDbDao extends AbstractDao<TasksDB, String> {

    @Autowired
    private TasksDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.TASKS_DB;
    }

    @Override
    protected MongoRepository<TasksDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<TasksDB> getEntityClass() {
        return TasksDB.class;
    }

    @Override
    public Map<String, TasksDB> findAllInMap() {
        String key = this.getRedisKey().getKey();
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        Map<String, TasksDB> map = new HashMap<>();
        entries.forEach((key1, value) -> {
            String newKey = key1.toString();
            String newJson = value.toString();
            TasksDB entity = JacksonUtils.parseObjectV2(newJson,TasksDB.class);
            map.put(newKey, entity);
        });
        return map;
    }
}
