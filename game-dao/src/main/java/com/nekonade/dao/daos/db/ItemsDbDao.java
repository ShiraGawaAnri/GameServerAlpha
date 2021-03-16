package com.nekonade.dao.daos.db;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.daos.AbstractDao;
import com.nekonade.dao.db.entity.data.ItemsDB;
import com.nekonade.dao.db.repository.ItemsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@CacheConfig(cacheManager = "caffeineCacheManager")
@Service
public class ItemsDbDao extends AbstractDao<ItemsDB, String> {

    @Autowired
    private ItemsDbRepository itemsDbRepository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.ITEMS_DB;
    }

    @Override
    protected MongoRepository<ItemsDB, String> getMongoRepository() {
        return itemsDbRepository;
    }

    @Override
    protected Class<ItemsDB> getEntityClass() {
        return ItemsDB.class;
    }

    @Cacheable(cacheNames = "ITEMS_DB", key = "#itemId", sync = true)
    public ItemsDB findItemDb(String itemId) {
        Query query = new Query(Criteria.where("itemId").is(itemId));
        return findByIdInMap(query, itemId, ItemsDB.class);
    }

}
