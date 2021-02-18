package com.nekonade.neko.service;


import com.nekonade.common.dto.RaidBattleDTO;
import com.nekonade.common.dto.RaidBattleRewardDTO;
import com.nekonade.common.model.PageResult;
import com.nekonade.common.utils.FunctionMapper;
import com.nekonade.dao.daos.GlobalConfigDao;
import com.nekonade.dao.daos.RaidBattleDbDao;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.dao.db.entity.RaidBattleReward;
import com.nekonade.dao.db.repository.RaidBattleDbRepository;
import com.nekonade.dao.helper.MongoPageHelper;
import com.nekonade.dao.helper.SortParam;
import com.nekonade.neko.common.DataConfigService;
import com.nekonade.network.param.game.message.neko.DoCreateBattleMsgRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service()
public class RaidBattleService {

//    public static final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> ALL_RAIDBATTLE_MAP = new ConcurrentHashMap<>();

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
    private MongoPageHelper mongoPageHelper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    public enum Constants {
        Unclaimed(0),
        Claimed(1)
        ;

        @Getter
        private final int type;

        Constants(int type) {
            this.type = type;
        }
    }

    public RaidBattle findRaidBattleDb(DoCreateBattleMsgRequest request) {
        DoCreateBattleMsgRequest.RequestBody bodyObj = request.getBodyObj();
        int area = bodyObj.getArea();
        int episode = bodyObj.getEpisode();
        int chapter = bodyObj.getChapter();
        int stage = bodyObj.getStage();
        int difficulty = bodyObj.getDifficulty();
        if (area == 0 || episode == 0 || chapter == 0 || stage == 0 || difficulty == 0) {
            return null;
        }
        return raidBattleDbDao.getRaidBattle(area, episode, chapter, stage, difficulty);
    }

    /*public PageResult<RaidBattleRewardDTO> findByPage(long playerId, Integer page, Integer limit) {
        *//*final Query query = new Query(Criteria.where("receiverId").is(playerId));
        Function<MailBox, MailDTO> mapper = FunctionMapper.Mapper(MailBox.class, MailDTO.class);
        return mongoPageHelper.pageQuery(query, MailBox.class, limit, page, sortParam, mapper);*//*
        String playerRewardSetKey = EnumRedisKey.RAIDBATTLE_REWARD_SET.getKey(String.valueOf(playerId));
        Set<String> members = redisTemplate.opsForSet().members(playerRewardSetKey);
        PageResult<RaidBattleRewardDTO> pageResult = new PageResult<>();
        if(members  == null || members.size() == 0){
            return pageResult;
        }
        List<String> unclaimedList = members.stream().filter(raidId -> {
            String playerRewardKey = EnumRedisKey.RAIDBATTLE_REWARD.getKey(raidId, String.valueOf(playerId));
            String rewardJson = redisTemplate.opsForValue().get(playerRewardKey);
            if(rewardJson == null){
                redisTemplate.opsForSet().remove(playerRewardSetKey,raidId);
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        int start = page * limit;
        int end = start + limit;
        if(start > unclaimedList.size()){
            return pageResult;
        }
        end = Math.min(end,unclaimedList.size());
        List<String> ids = new ArrayList<>();
        for(int i =0;i < end;i++){
            String raidId = unclaimedList.get(i);
            String playerRewardKey = EnumRedisKey.RAIDBATTLE_REWARD.getKey(raidId, String.valueOf(playerId));
            ids.add(playerRewardKey);
        }
        List<String> resultJsonList = redisTemplate.opsForValue().multiGet(ids);
        List<RaidBattleRewardDTO> rewardDTOList = new ArrayList<>();
        if(resultJsonList == null){
            return pageResult;
        }
        resultJsonList.forEach(entityJson->{
            rewardDTOList.add(JSON.parseObject(entityJson,RaidBattleRewardDTO.class));
        });
        rewardDTOList.sort(Comparator.comparingLong(RaidBattleRewardDTO::getTimestamp));
        //写入报酬
        redisTemplate.opsForValue().setIfAbsent(playerRewardKey,rewardJson,EnumRedisKey.RAIDBATTLE_REWARD.getTimeout());
        //写入到该玩家的报酬Set，方便查询未领取的报酬
        redisTemplate.opsForSet().add(playerRewardSetKey,raidId);
    }*/

    public PageResult<RaidBattleDTO> findRaidBattleHistoryByPage(long playerId, Integer page, Integer limit) {
        SortParam sortParam = new SortParam();
        sortParam.setSortDirection(Sort.Direction.DESC);
        /*RaidBattle example = new RaidBattle();
        RaidBattle.Player player = new RaidBattle.Player();
        player.setPlayerId(playerId);
        example.setFinish(true);
        example.getPlayers().add(player);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnorePaths("items");
        Example<RaidBattle> queryEntity = Example.of(example, matcher);
        Criteria criteria = Criteria.byExample(queryEntity);
        final Query query = new Query().addCriteria(criteria);*/

        Criteria criteria = Criteria.where("finish").is(true).and("players").elemMatch(Criteria.where("playerId").is(playerId));
        final Query query = new Query(criteria);
        Function<RaidBattle, RaidBattleDTO> mapper = FunctionMapper.Mapper(RaidBattle.class, RaidBattleDTO.class);
        return mongoPageHelper.pageQuery(query, RaidBattle.class, limit, page, sortParam,mapper);
    }

    public PageResult<RaidBattleRewardDTO> findUnclaimedRewardByPage(long playerId, Integer page, Integer limit) {
        SortParam sortParam = new SortParam();
        Constants type = Constants.Unclaimed;
        return findRewardByPage(playerId,type.getType(),page,limit,sortParam);
    }
    public PageResult<RaidBattleRewardDTO> findClaimedRewardByPage(long playerId, Integer page, Integer limit) {
        SortParam sortParam = new SortParam();
        sortParam.setSortDirection(Sort.Direction.DESC);
        Constants type = Constants.Claimed;
        return findRewardByPage(playerId,type.getType(),page,limit,sortParam);
    }

    private PageResult<RaidBattleRewardDTO> findRewardByPage(long playerId, int type, Integer page, Integer limit, SortParam sortParam) {
        RaidBattleReward example = new RaidBattleReward();
        example.setPlayerId(playerId);
        example.setClaimed(type);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues().withIgnorePaths("items").withIgnorePaths("timestamp");
        Example<RaidBattleReward> queryEntity = Example.of(example, matcher);
        Criteria criteria = Criteria.byExample(queryEntity);
        final Query query = new Query().addCriteria(criteria);
        /*final Query query = new Query(Criteria.where("playerId").is(playerId).and("claimed").is(type));*/
        Function<RaidBattleReward, RaidBattleRewardDTO> mapper = FunctionMapper.Mapper(RaidBattleReward.class, RaidBattleRewardDTO.class);
        return mongoPageHelper.pageQuery(query, RaidBattleReward.class, limit, page, sortParam,mapper);
    }
}
