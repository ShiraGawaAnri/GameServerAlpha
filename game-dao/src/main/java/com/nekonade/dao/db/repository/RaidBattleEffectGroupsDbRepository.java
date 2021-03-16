package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.rbeffect.RaidBattleEffectGroupsDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RaidBattleEffectGroupsDbRepository extends MongoRepository<RaidBattleEffectGroupsDB, String> {
    void deleteByEffectGroupId(String effectGroupId);
}
