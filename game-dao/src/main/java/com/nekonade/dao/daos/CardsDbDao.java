package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.CardsDB;
import com.nekonade.dao.db.repository.CardsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class CardsDbDao extends AbstractDao<CardsDB,String>{

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
}
