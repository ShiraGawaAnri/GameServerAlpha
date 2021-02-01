package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.ItemsDB;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItemsDbRepository extends MongoRepository<ItemsDB, Long> {

    void deleteByItemId(String itemId);

    Optional<ItemsDB> findByItemId(String itemId);
}
