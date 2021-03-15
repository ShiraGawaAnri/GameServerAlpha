package com.nekonade.dao.daos;


import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.RaidBattleEffectsDB;
import com.nekonade.dao.db.repository.RaidBattleEffectsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class RaidBattleEffectsDbDao extends AbstractDao<RaidBattleEffectsDB, String> {

    @Autowired
    private RaidBattleEffectsDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.RAIDBATTLE_EFFECTS_DB;
    }

    @Override
    protected MongoRepository<RaidBattleEffectsDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<RaidBattleEffectsDB> getEntityClass() {
        return RaidBattleEffectsDB.class;
    }
}
