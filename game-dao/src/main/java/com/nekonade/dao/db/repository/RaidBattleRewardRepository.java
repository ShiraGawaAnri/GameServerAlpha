package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.RaidBattleReward;
import com.nekonade.dao.db.entity.data.RewardsDB;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RaidBattleRewardRepository extends MongoRepository<RaidBattleReward,String> {

    void deleteByPlayerIdAndRaidId(long playerId,String raidId);

    Optional<RaidBattleReward> findByPlayerIdAndRaidId(long playerId,String raidId);
}