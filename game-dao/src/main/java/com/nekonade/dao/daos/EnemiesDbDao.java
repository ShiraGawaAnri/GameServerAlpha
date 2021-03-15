package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.EnemiesDB;
import com.nekonade.dao.db.repository.EnemiesDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@CacheConfig(cacheManager = "caffeineCacheManager")
@Service
public class EnemiesDbDao extends AbstractDao<EnemiesDB, String> {

    @Autowired
    private EnemiesDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.ENEMIES_DB;
    }

    @Override
    protected MongoRepository<EnemiesDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<EnemiesDB> getEntityClass() {
        return EnemiesDB.class;
    }

    @Cacheable(cacheNames = "ENEMIES_DB", key = "#monsterId", sync = true)
    public EnemiesDB findByMonsterId(String monsterId) {
        Query query = new Query(Criteria.where("monsterId").is(monsterId));
        return findByIdInMap(query, monsterId, EnemiesDB.class);
    }

    @CacheEvict(cacheNames = "ENEMIES_DB}", allEntries = true)
    public void deleteCache() {

    }
}
