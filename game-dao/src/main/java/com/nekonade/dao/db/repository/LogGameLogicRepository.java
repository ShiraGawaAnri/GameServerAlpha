package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.LogGameLogic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogGameLogicRepository extends MongoRepository<LogGameLogic, String> {
}
