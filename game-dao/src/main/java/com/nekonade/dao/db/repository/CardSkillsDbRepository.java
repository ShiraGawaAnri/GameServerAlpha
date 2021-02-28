package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.ActiveSkillsDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CardSkillsDbRepository extends MongoRepository<ActiveSkillsDB,String> {
}
