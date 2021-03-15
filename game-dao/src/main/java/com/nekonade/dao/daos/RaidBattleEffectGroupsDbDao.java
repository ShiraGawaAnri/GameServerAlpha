package com.nekonade.dao.daos;


import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.RaidBattleEffectGroupsDB;
import com.nekonade.dao.db.repository.RaidBattleEffectGroupsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class RaidBattleEffectGroupsDbDao extends AbstractDao<RaidBattleEffectGroupsDB, String> {

    @Autowired
    private RaidBattleEffectGroupsDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.RAIDBATTLE_EFFECT_GROUPS_DB;
    }

    @Override
    protected MongoRepository<RaidBattleEffectGroupsDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<RaidBattleEffectGroupsDB> getEntityClass() {
        return RaidBattleEffectGroupsDB.class;
    }
}
