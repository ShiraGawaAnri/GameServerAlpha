package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.CardSkillsDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CardSkillsDbRepository extends MongoRepository<CardSkillsDB,String> {
}
