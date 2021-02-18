package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.EnemiesDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EnemiesDbRepository extends MongoRepository<EnemiesDB,String> {
    void deleteByMonsterId(String monsterId);
}
