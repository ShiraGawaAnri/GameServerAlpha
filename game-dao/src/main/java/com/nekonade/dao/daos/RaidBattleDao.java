package com.nekonade.dao.daos;

import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.dao.db.repository.RaidBattleRepository;
import com.nekonade.common.redis.EnumRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class RaidBattleDao extends AbstractDao<RaidBattle, String> {
    @Autowired
    private RaidBattleRepository raidBattleRepository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.ARENA;
    }

    @Override
    protected MongoRepository<RaidBattle, String> getMongoRepository() {
        return raidBattleRepository;
    }

    @Override
    protected Class<RaidBattle> getEntityClass() {
        return RaidBattle.class;
    }


}
