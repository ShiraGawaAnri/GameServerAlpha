package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.EnemiesDB;
import com.nekonade.dao.db.repository.EnemiesDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class EnemiesDbDao extends AbstractDao<EnemiesDB, String>{

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

    public EnemiesDB findByMonsterId(String monsterId){
        Query query = new Query(Criteria.where("monsterId").is(monsterId));
        return findByIdInMap(query,monsterId,EnemiesDB.class);
    }
}
