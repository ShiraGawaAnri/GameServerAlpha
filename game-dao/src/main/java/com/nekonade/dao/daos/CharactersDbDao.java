package com.nekonade.dao.daos;

import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.data.CharactersDB;
import com.nekonade.dao.db.repository.CharactersDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class CharactersDbDao extends AbstractDao<CharactersDB, String>{

    @Autowired
    private CharactersDbRepository repository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.CHARACTERS_DB;
    }

    @Override
    protected MongoRepository<CharactersDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<CharactersDB> getEntityClass() {
        return CharactersDB.class;
    }

    public CharactersDB findChara(String charaId){
        Query query = new Query(Criteria.where("charId").is(charaId));
        return this.findByIdInMap(query, charaId, CharactersDB.class);
    }
}
