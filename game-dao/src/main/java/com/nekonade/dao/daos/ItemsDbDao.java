package com.nekonade.dao.daos;

import com.nekonade.dao.db.entity.data.ItemsDB;
import com.nekonade.dao.db.repository.ItemsDbRepository;
import com.nekonade.common.redis.EnumRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemsDbDao extends AbstractDao<ItemsDB, String> {

    @Autowired
    private ItemsDbRepository itemsDbRepository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.ITEMSDB;
    }

    @Override
    protected MongoRepository<ItemsDB, String> getMongoRepository() {
        return itemsDbRepository;
    }

    @Override
    protected Class<ItemsDB> getEntityClass() {
        return ItemsDB.class;
    }

}
