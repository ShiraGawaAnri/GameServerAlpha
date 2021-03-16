package com.nekonade.dao.daos.db;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.daos.AbstractDao;
import com.nekonade.dao.db.entity.data.task.TasksDB;
import com.nekonade.dao.db.repository.TasksDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

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
}
