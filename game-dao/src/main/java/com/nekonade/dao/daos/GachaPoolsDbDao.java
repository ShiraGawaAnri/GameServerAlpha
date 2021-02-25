package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.GachaPoolsDB;
import com.nekonade.dao.db.repository.GachaPoolsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class GachaPoolsDbDao extends AbstractDao<GachaPoolsDB,String>{

    @Autowired
    private GachaPoolsDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return null;
    }

    @Override
    protected MongoRepository<GachaPoolsDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<GachaPoolsDB> getEntityClass() {
        return GachaPoolsDB.class;
    }
}
