package com.nekonade.dao.daos.db;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.daos.AbstractDao;
import com.nekonade.dao.db.entity.data.CardsDB;
import com.nekonade.dao.db.entity.data.task.TasksDB;
import com.nekonade.dao.db.repository.TasksDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    @Cacheable(cacheNames = "TASKS_DB",key = "#taskId",sync = true)
    public TasksDB findTasksDb(String taskId){
        Query query = new Query(Criteria.where("taskId").is(taskId));
        return this.findByIdInMap(query, taskId, TasksDB.class);
    }
}
