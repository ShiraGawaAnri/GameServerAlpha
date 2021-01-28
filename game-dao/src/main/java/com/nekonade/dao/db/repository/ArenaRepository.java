package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.Arena;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArenaRepository extends MongoRepository<Arena, Long> {

}
