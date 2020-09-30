package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerRepository extends MongoRepository<Player, Long> {

}
