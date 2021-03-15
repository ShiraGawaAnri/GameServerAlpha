package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.CardsDB;
import com.nekonade.dao.db.repository.CardsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@CacheConfig(cacheManager = "caffeineCacheManager")
@Service
public class CardsDbDao extends AbstractDao<CardsDB, String> {

    @Autowired
    private CardsDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.CARDS_DB;
    }

    @Override
    protected MongoRepository<CardsDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<CardsDB> getEntityClass() {
        return CardsDB.class;
    }

    @Cacheable(cacheNames = "CARDS_DB", key = "#cardId", sync = true)
    public CardsDB findCardsDB(String cardId) {
        Query query = new Query(Criteria.where("cardId").is(cardId));
        return this.findByIdInMap(query, cardId, CardsDB.class);
    }

    //@Cacheable(cacheNames = "CARDS_DB_ALL")
    public Map<String, CardsDB> findAllCardsDB() {
        return this.findAllInMap();
    }
}
