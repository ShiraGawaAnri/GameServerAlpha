package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.CardSkillsDB;
import com.nekonade.dao.db.entity.data.CharactersDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CharactersDbRepository extends MongoRepository<CharactersDB,String> {
}
