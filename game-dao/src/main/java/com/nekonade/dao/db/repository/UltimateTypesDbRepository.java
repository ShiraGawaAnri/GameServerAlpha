package com.nekonade.dao.db.repository;


import com.nekonade.dao.db.entity.data.UltimateTypesDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UltimateTypesDbRepository extends MongoRepository<UltimateTypesDB,String> {
}
