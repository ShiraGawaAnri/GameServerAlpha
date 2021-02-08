package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.RaidBattle;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RaidBattleRepository extends MongoRepository<RaidBattle, String> {

}
