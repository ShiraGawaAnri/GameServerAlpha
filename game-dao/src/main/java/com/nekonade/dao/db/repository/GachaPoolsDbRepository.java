package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.GachaPoolsDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GachaPoolsDbRepository extends MongoRepository<GachaPoolsDB,String> {
}
