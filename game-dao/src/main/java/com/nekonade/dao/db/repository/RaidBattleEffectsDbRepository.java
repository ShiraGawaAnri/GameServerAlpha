package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.RaidBattleEffectsDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RaidBattleEffectsDbRepository extends MongoRepository<RaidBattleEffectsDB, String> {
    void deleteByEffectId(String effectId);
}
