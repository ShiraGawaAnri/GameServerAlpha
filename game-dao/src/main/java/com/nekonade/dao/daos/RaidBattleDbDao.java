package com.nekonade.dao.daos;

import com.nekonade.common.dto.RaidBattle;
import com.nekonade.dao.db.entity.data.ItemsDB;
import com.nekonade.dao.db.entity.data.RaidBattleDB;
import com.nekonade.dao.db.repository.ItemsDbRepository;
import com.nekonade.dao.db.repository.RaidBattleDbRepository;
import com.nekonade.dao.redis.EnumRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class RaidBattleDbDao extends AbstractDao<RaidBattleDB, String>{

    @Autowired
    private RaidBattleDbRepository repository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.RAIDBATTLEDB;
    }

    @Override
    protected MongoRepository<RaidBattleDB, String> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<RaidBattleDB> getEntityClass() {
        return RaidBattleDB.class;
    }

    private String createStageRedisKey(String[] list){
        return "STAGE_" + String.join("_", Arrays.asList(list));
    }

    public RaidBattleDB findRaidBattleDb(int area,int episode,int chapter,int stage,int difficulty){
        String[] list = new String[]{String.valueOf(area),String.valueOf(episode),String.valueOf(chapter),String.valueOf(stage),String.valueOf(difficulty)};
        String key = EnumRedisKey.RAIDBATTLEDB.getKey();
        String stageKey = createStageRedisKey(list);
        Object value = stringRedisTemplate.opsForHash().get(key, stageKey);
        if(value != null){
            return (RaidBattleDB) value;
        }
        RaidBattleDB raidBattleDB = new RaidBattleDB();
        raidBattleDB.setArea(area);
        raidBattleDB.setEpisode(episode);
        raidBattleDB.setChapter(chapter);
        raidBattleDB.setStage(stage);
        ExampleMatcher matcher = ExampleMatcher.matching().withIncludeNullValues();
        Example<RaidBattleDB> queryEntity = Example.of(raidBattleDB, matcher);
        Optional<RaidBattleDB> op = repository.findOne(queryEntity);
        return op.orElse(null);
    }
}
