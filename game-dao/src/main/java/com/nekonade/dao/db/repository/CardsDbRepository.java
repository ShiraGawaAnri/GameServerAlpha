package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.CardSkillsDB;
import com.nekonade.dao.db.entity.data.CardsDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CardsDbRepository extends MongoRepository<CardsDB,String> {
}
