package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.CardSkillsDB;
import com.nekonade.dao.db.entity.data.CharactersDB;
import com.nekonade.dao.db.repository.CardSkillsDbRepository;
import com.nekonade.dao.db.repository.CharactersDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class CharactersDbDao extends AbstractDao<CharactersDB, String>{

    @Autowired
    private CharactersDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.CARDSKILLS_DB;
    }

    @Override
    protected MongoRepository<CharactersDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<CharactersDB> getEntityClass() {
        return CharactersDB.class;
    }
}
