package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.ActiveSkillsDB;
import com.nekonade.dao.db.repository.CardSkillsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class CardSkillsDbDao extends AbstractDao<ActiveSkillsDB, String>{

    @Autowired
    private CardSkillsDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.CARDSKILLS_DB;
    }

    @Override
    protected MongoRepository<ActiveSkillsDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<ActiveSkillsDB> getEntityClass() {
        return ActiveSkillsDB.class;
    }
}
