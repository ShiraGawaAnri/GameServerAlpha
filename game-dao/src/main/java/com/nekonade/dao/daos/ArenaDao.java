package com.nekonade.dao.daos;

import com.nekonade.dao.db.entity.Arena;
import com.nekonade.dao.db.repository.ArenaRepository;
import com.nekonade.dao.redis.EnumRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class ArenaDao extends AbstractDao<Arena, Long> {
    @Autowired
    private ArenaRepository arenaRepository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.ARENA;
    }

    @Override
    protected MongoRepository<Arena, Long> getMongoRepository() {
        return arenaRepository;
    }

    @Override
    protected Class<Arena> getEntityClass() {
        return Arena.class;
    }


}
