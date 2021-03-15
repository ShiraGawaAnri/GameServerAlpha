package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.RewardsDB;
import com.nekonade.dao.db.repository.RewardsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class RewardsDbDao extends AbstractDao<RewardsDB, String> {

    @Autowired
    private RewardsDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.REWARDS_DB;
    }

    @Override
    protected MongoRepository<RewardsDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<RewardsDB> getEntityClass() {
        return RewardsDB.class;
    }
}
