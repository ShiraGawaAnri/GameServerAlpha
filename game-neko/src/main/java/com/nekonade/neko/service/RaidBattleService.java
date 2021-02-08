package com.nekonade.neko.service;


import com.nekonade.common.dto.RaidBattle;
import com.nekonade.dao.daos.GlobalConfigDao;
import com.nekonade.dao.daos.RaidBattleDbDao;
import com.nekonade.dao.db.entity.data.RaidBattleDB;
import com.nekonade.dao.db.repository.RaidBattleDbRepository;
import com.nekonade.neko.common.DataConfigService;
import com.nekonade.network.param.game.message.neko.CreateBattleMsgRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RaidBattleService {

    @Autowired
    private DataConfigService dataConfigService;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private GlobalConfigDao globalConfigDao;
    @Autowired
    private RaidBattleDbDao raidBattleDbDao;
    @Autowired
    private RaidBattleDbRepository raidBattleDbRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public RaidBattle findRaidBattleDb(long playerId, CreateBattleMsgRequest request) {
        CreateBattleMsgRequest.RequestBody bodyObj = request.getBodyObj();
        int area = bodyObj.getArea();
        int episode = bodyObj.getEpisode();
        int chapter = bodyObj.getChapter();
        int stage = bodyObj.getStage();
        int difficulty = bodyObj.getDifficulty();
        if (area == 0 || episode == 0 || chapter == 0 || stage == 0 || difficulty == 0) {
            return null;
        }
        RaidBattleDB result = raidBattleDbDao.findRaidBattleDb(area, episode, chapter, stage, difficulty);
        if (result == null) {
            return null;
        }
        RaidBattle raidBattle = new RaidBattle();
        BeanUtils.copyProperties(result, raidBattle);
        raidBattle.setRestTime(result.getLimitTime());
        return raidBattle;
    }
}
