package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.LogGameLogic;
import com.nekonade.dao.db.entity.data.ItemsDB;
import com.nekonade.dao.db.repository.LogGameLogicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class LogGameLogicDao extends AbstractDao<LogGameLogic, String> {

    @Autowired
    private LogGameLogicRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return null;
    }

    @Override
    protected MongoRepository<LogGameLogic, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<LogGameLogic> getEntityClass() {
        return LogGameLogic.class;
    }

    public void saveLog(LogGameLogic entity){
        this.saveOrUpdateToDB(entity);
    }
}
