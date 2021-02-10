package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.RaidBattle;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RaidBattleRepository extends MongoRepository<RaidBattle, String> {

    Optional<RaidBattle> findByRaidId(String raidId);

    Optional<RaidBattle> findByRaidIdAndFinishAndExpiredBefore(String raidId,Boolean finish,long now);
}
