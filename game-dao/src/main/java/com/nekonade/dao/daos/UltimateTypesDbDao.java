package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.RaidBattleEffectsDB;
import com.nekonade.dao.db.entity.data.UltimateTypesDB;
import com.nekonade.dao.db.repository.UltimateTypesDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class UltimateTypesDbDao extends AbstractDao<UltimateTypesDB, String>{

    @Autowired
    private UltimateTypesDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.ULTIMATETYPES_DB;
    }

    @Override
    protected MongoRepository<UltimateTypesDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<UltimateTypesDB> getEntityClass() {
        return UltimateTypesDB.class;
    }
}
