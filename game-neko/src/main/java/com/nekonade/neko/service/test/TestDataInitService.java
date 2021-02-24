package com.nekonade.neko.service.test;


import com.nekonade.common.dto.ItemDTO;
import com.nekonade.common.utils.FunctionMapper;
import com.nekonade.dao.daos.*;
import com.nekonade.dao.db.entity.MailBox;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.data.*;
import com.nekonade.dao.db.repository.*;
import com.nekonade.neko.service.ItemDbService;
import com.nekonade.network.message.event.function.EnterGameEvent;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@EnableScheduling
public class TestDataInitService {

    @Autowired
    private ItemDbService itemDbService;

    @Autowired
    private ItemsDbRepository itemsDbRepository;

    @Autowired
    private GlobalConfigDao globalConfigDao;

    @Autowired
    private ItemsDbDao itemsDbDao;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MailBoxRepository mailBoxRepository;

    @Autowired
    private RaidBattleDbRepository raidBattleDbRepository;

    @Autowired
    private RaidBattleDbDao raidBattleDbDao;

    @Autowired
    private EnemiesDbRepository enemiesDbRepository;

    @Autowired
    private EnemiesDbDao enemiesDbDao;

    @Autowired
    private RewardsDbRepository rewardsDbRepository;

    @Autowired
    private RewardsDbDao rewardsDbDao;

    @Autowired
    private RaidBattleEffectsDbDao raidBattleEffectsDbDao;

    @Autowired
    private RaidBattleEffectsDbRepository raidBattleEffectsDbRepository;

    @Autowired
    private RaidBattleEffectGroupsDbDao raidBattleEffectGroupsDbDao;

    @Autowired
    private RaidBattleEffectGroupsDbRepository raidBattleEffectGroupsDbRepository;

    @Autowired
    private UltimateTypesDbDao ultimateTypesDbDao;

    @Autowired
    private UltimateTypesDbRepository ultimateTypesDbRepository;

    @Autowired
    private CardSkillsDbDao cardSkillsDbDao;

    @Autowired
    private CardSkillsDbRepository cardSkillsDbRepository;

    @DataProvider(name = "ItemDbTestData")
    public static Object[][] ItemDbTestData() {
        ItemsDB itemsDB1 = new ItemsDB();
        itemsDB1.setItemId("1");
        itemsDB1.setName("Stamina回复药");
        itemsDB1.setCategory(1);
        itemsDB1.setType(2);
        ItemsDB itemsDB2 = new ItemsDB();
        itemsDB2.setItemId("2");
        itemsDB2.setName("BP回复药");
        itemsDB2.setCategory(1);
        itemsDB2.setType(2);
        ItemsDB itemsDB3 = new ItemsDB();
        itemsDB3.setItemId("3");
        itemsDB3.setName("奖励宝箱A");
        itemsDB3.setCategory(3);
        itemsDB3.setType(5);
        ItemsDB itemsDB4 = new ItemsDB();
        itemsDB4.setItemId("4");
        itemsDB4.setName("普通素材A");
        itemsDB4.setCategory(2);
        itemsDB4.setType(1);
        ItemsDB itemsDB5 = new ItemsDB();
        itemsDB5.setItemId("5");
        itemsDB5.setName("珍贵素材B");
        itemsDB5.setCategory(2);
        itemsDB5.setType(2);
        itemsDB5.setMaxStack(100L);
        return new Object[][]{
                {itemsDB1}, {itemsDB2}, {itemsDB3},
                {itemsDB4}, {itemsDB5}
        };
    }

    @EventListener
    public void loginAddExp(EnterGameEvent event) {
        event.getPlayerManager().getExperienceManager().addExperience(1000);
    }

    /*@EventListener
    public void addItem(EnterGameEvent event) {
        InventoryManager inventoryManager = event.getPlayerManager().getInventoryManager();
        List<ItemsDB> itemDbData = getItemDbData();
        itemDbData.forEach(item->{
            Random random = new Random();
            inventoryManager.produceItem(item.getItemId(), random.nextInt(20));
        });
    }*/

    private void InitItemsDB(){
        Object[][] objects = ItemDbTestData();
        List<ItemsDB> itemsDBS = new ArrayList<>();
        for (Object[] object : objects) {
            for (Object obj : object) {
                ItemsDB item = (ItemsDB) obj;
                itemsDBS.add(item);
            }
        }
        itemsDBS.forEach(item->{
            itemsDbRepository.deleteByItemId(item.getItemId());
            itemsDbDao.saveOrUpdateMap(item, item.getItemId());
        });
    }

    private List<ItemsDB> getItemDbData(){
        return itemsDbRepository.findAll();
    }
    private ScheduledFuture<?> scheduledFuture;
    private final DefaultEventExecutor eventExecutors = new DefaultEventExecutor();

    @PostConstruct
    private void init() {

        scheduledFuture = eventExecutors.scheduleWithFixedDelay(() -> {
            InitItemsDB();
            InitEnemiesDB();
            InitRewardsDB();
            InitRaidBattleDB();
            SendMail();
            InitRaidBattleEffectGroupsDB();
            InitRaidBattleEffectsDB();
            InitUltimateTypesDB();
            InitCardSkillsDB();

            if(scheduledFuture != null){
                scheduledFuture.cancel(true);
            }
        }, 5000, 5000, TimeUnit.MILLISECONDS);

        //mailBoxRepository.saveAll(mailBoxes);
    }

    private void SendMail() {
        List<MailBox> mailBoxes = new ArrayList<>();
        Random random = new Random();
        int times = random.nextInt(3) + 5;
        for (int i = 0; i < times; i++) {
            mailBoxes.addAll(MailBoxTestData());
        }
        mailBoxes.forEach(each -> mailBoxRepository.save(each));
    }

    @Test(dataProvider = "ItemDbTestData")
    private void LoadItemDb(ItemsDB itemsDB) {
//        itemDbService.addItemDb(itemsDB);
//        ItemsDB op = itemDbService.findByItemId(itemsDB.getItemId());
//        Assert.assertNotNull(op);
    }

    private List<MailBox> MailBoxTestData() {
        List<Player> all = playerRepository.findAll();
        Player player = all.get(0);
        long senderId = player.getPlayerId();
        String senderName = player.getNickName();
        List<ItemsDB> items = itemsDbRepository.findAll();
        long now = System.currentTimeMillis();
        return all.stream().map(Player::getPlayerId).map(id -> {
            MailBox mailBox = new MailBox();
            mailBox.setSenderId(senderId);
            mailBox.setSenderName(senderName);
            mailBox.setTitle(DigestUtils.md5Hex(id + senderName + player + Math.random()));
            mailBox.setContent("Send To PlayerId:" + id);
            mailBox.setTimestamp(now);
            mailBox.setExpired(now + Duration.ofDays(30).toMillis());
            mailBox.setReceiverId(id);
            Function<ItemsDB, ItemDTO> mapper = FunctionMapper.Mapper(ItemsDB.class, ItemDTO.class);
            Collections.shuffle(items);
            List<ItemDTO> list = items.stream().map(mapper).peek(each -> {
                Random random = new Random();
                each.setAmount(random.nextInt(10) + 1);
            }).collect(Collectors.toList());
            mailBox.setGifts(list);
            return mailBox;
        }).collect(Collectors.toList());
    }


    private String createStageRedisKey(String[] list) {
        return "STAGE_" + String.join("_", Arrays.asList(list));
    }

    private void InitRaidBattleDB(){
        List<EnemiesDB> enemiesDBS = getEnemiesDB();
        RaidBattleDB raidBattleDB = new RaidBattleDB();
        raidBattleDB.setArea(1);
        raidBattleDB.setEpisode(1);
        raidBattleDB.setChapter(1);
        raidBattleDB.setStage(1);
        raidBattleDB.setDifficulty(1);
        raidBattleDB.setCostStaminaPoint(10);
        raidBattleDB.setMultiRaid(false);
        String[] r = new String[]{"1", "1", "1", "1", "1"};
        String rkey = createStageRedisKey(r);
        raidBattleDB.setStageId(rkey);
        {

            CopyOnWriteArrayList<String> enemyIds = new CopyOnWriteArrayList<>();

            Optional<EnemiesDB> test_monster_0001 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0001")).findFirst();
            if(test_monster_0001.isPresent()){
                enemyIds.add("TEST_MONSTER_0001");
                raidBattleDB.getEnemyList().add(test_monster_0001.get());
            }

            Optional<EnemiesDB> test_monster_0002 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0002")).findFirst();
            if(test_monster_0002.isPresent()){
                enemyIds.add("TEST_MONSTER_0002");
                raidBattleDB.getEnemyList().add(test_monster_0002.get());
            }
            raidBattleDB.setEnemyIds(enemyIds);
        }

        RaidBattleDB raidBattleDB1 = new RaidBattleDB();
        raidBattleDB1.setArea(1);
        raidBattleDB1.setEpisode(1);
        raidBattleDB1.setChapter(1);
        raidBattleDB1.setStage(2);
        raidBattleDB1.setDifficulty(1);
        Map<String, Integer> costItemMap1 = new HashMap<>();
        costItemMap1.put("4", 2);
        costItemMap1.put("5", 13);
        raidBattleDB1.setCostItemMap(costItemMap1);
        raidBattleDB1.setCostStaminaPoint(15);
        raidBattleDB1.setMultiRaid(true);
        raidBattleDB1.setLimitCounter(5L);
        raidBattleDB1.setLimitCounterRefreshType(0);
        String[] r1 = new String[]{"1", "1", "1", "2", "1"};
        String rkey1 = createStageRedisKey(r1);
        raidBattleDB1.setStageId(rkey1);
        {
            CopyOnWriteArrayList<String> enemyIds = new CopyOnWriteArrayList<>();

            Optional<EnemiesDB> test_monster_0003 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0003")).findFirst();
            if(test_monster_0003.isPresent()){
                enemyIds.add("TEST_MONSTER_0003");
                raidBattleDB1.getEnemyList().add(test_monster_0003.get());
            }

            Optional<EnemiesDB> test_monster_0004 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0004")).findFirst();
            if(test_monster_0004.isPresent()){
                enemyIds.add("TEST_MONSTER_0004");
                raidBattleDB1.getEnemyList().add(test_monster_0004.get());
            }
            raidBattleDB1.setEnemyIds(enemyIds);
        }


        RaidBattleDB raidBattleDB2 = new RaidBattleDB();
        raidBattleDB2.setArea(1);
        raidBattleDB2.setEpisode(1);
        raidBattleDB2.setChapter(1);
        raidBattleDB2.setStage(3);
        raidBattleDB2.setDifficulty(1);
        Map<String, Integer> costItemMap2 = new HashMap<>();
        costItemMap2.put("4", 10);
        costItemMap2.put("5", 5);
        raidBattleDB2.setCostItemMap(costItemMap2);
        raidBattleDB2.setCostStaminaPoint(15);
        raidBattleDB2.setMultiRaid(false);
        raidBattleDB2.setLimitCounter(10L);
        raidBattleDB2.setLimitCounterRefreshType(2);
        String[] r2 = new String[]{"1", "1", "1", "3", "1"};
        String rkey2 = createStageRedisKey(r2);
        raidBattleDB2.setStageId(rkey2);

        {
            CopyOnWriteArrayList<String> enemyIds = new CopyOnWriteArrayList<>();

            Optional<EnemiesDB> test_monster_0005 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0005")).findFirst();
            if(test_monster_0005.isPresent()){
                enemyIds.add("TEST_MONSTER_0005");
                raidBattleDB2.getEnemyList().add(test_monster_0005.get());
            }
            raidBattleDB2.setEnemyIds(enemyIds);
        }

        RaidBattleDB raidBattleDB3 = new RaidBattleDB();
        raidBattleDB3.setArea(1);
        raidBattleDB3.setEpisode(1);
        raidBattleDB3.setChapter(1);
        raidBattleDB3.setStage(4);
        raidBattleDB3.setDifficulty(1);
        Map<String, Integer> costItemMap3 = new HashMap<>();
        raidBattleDB3.setCostItemMap(costItemMap2);
        raidBattleDB3.setCostStaminaPoint(100);
        raidBattleDB3.setMultiRaid(false);
        raidBattleDB3.setLimitCounter(2L);
        raidBattleDB3.setLimitCounterRefreshType(6);
        String[] r3 = new String[]{"1", "1", "1", "4", "1"};
        String rkey3 = createStageRedisKey(r3);
        raidBattleDB3.setStageId(rkey3);

        {
            CopyOnWriteArrayList<String> enemyIds = new CopyOnWriteArrayList<>();

            Optional<EnemiesDB> test_monster_0005 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0005")).findFirst();
            if(test_monster_0005.isPresent()){
                enemyIds.add("TEST_MONSTER_0005");
                raidBattleDB3.getEnemyList().add(test_monster_0005.get());
            }
            raidBattleDB3.setEnemyIds(enemyIds);
        }

        List<RaidBattleDB> raidBattleDBS = Stream.of(raidBattleDB,raidBattleDB2,raidBattleDB3).collect(Collectors.toList());

        //添加奖励
        List<ItemsDB> itemsDBS = getItemDbData();
        raidBattleDBS.forEach(each -> {
            String[] list = new String[]{
                    String.valueOf(each.getArea()),
                    String.valueOf(each.getEpisode()),
                    String.valueOf(each.getChapter()),
                    String.valueOf(each.getStage()),
                    String.valueOf(each.getDifficulty()),
            };
            RewardsDB rewardsDB = new RewardsDB();
            Random random = new Random();
            int rand = random.nextInt(8);
            for (int i = 0;i <= rand;i++){
                RewardsDB.Item item = new RewardsDB.Item();
                BeanUtils.copyProperties(itemsDBS.get(0),item);
                rewardsDB.setRewardId(each.getStageId());
                item.setAmount(random.nextInt(5));
                item.setProb(random.nextDouble());
                Collections.shuffle(itemsDBS);
                rewardsDB.getItems().add(item);
            }
            rewardsDB.makeItem();
            each.setReward(rewardsDB);
            String stageRedisKey = createStageRedisKey(list);
            raidBattleDbRepository.deleteByStageId(stageRedisKey);
            raidBattleDbDao.saveOrUpdateMap(each, stageRedisKey);
            rewardsDbRepository.deleteByRewardId(rewardsDB.getRewardId());
            rewardsDbDao.saveOrUpdate(rewardsDB,rewardsDB.getRewardId());
        });
    }

    private List<RaidBattleDB> getRaidBattleDB() {

        return raidBattleDbRepository.findAll();
    }

    private void InitEnemiesDB(){
        EnemiesDB enemiesDB = new EnemiesDB();
        enemiesDB.setMonsterId("TEST_MONSTER_0001");
        enemiesDB.setName("测试怪物1");

        EnemiesDB enemiesDB2 = new EnemiesDB();
        enemiesDB2.setMonsterId("TEST_MONSTER_0002");
        enemiesDB2.setName("测试怪物2");
        enemiesDB2.setMaxHp(1500);


        EnemiesDB enemiesDB3 = new EnemiesDB();
        enemiesDB3.setMonsterId("TEST_MONSTER_0003");
        enemiesDB3.setName("测试怪物3");

        EnemiesDB enemiesDB4 = new EnemiesDB();
        enemiesDB4.setMonsterId("TEST_MONSTER_0004");
        enemiesDB4.setName("测试怪物4");
        enemiesDB4.setMaxHp(10000);
        enemiesDB4.setKey(1);

        EnemiesDB enemiesDB5 = new EnemiesDB();
        enemiesDB5.setMonsterId("TEST_MONSTER_0005");
        enemiesDB5.setName("测试怪物5");
        enemiesDB5.setMaxHp(5000);

        List<EnemiesDB> list = Stream.of(enemiesDB,enemiesDB2,enemiesDB3,enemiesDB4,enemiesDB5).collect(Collectors.toList());

        list.forEach(each->{
            enemiesDbRepository.deleteByMonsterId(each.getMonsterId());
            enemiesDbDao.saveOrUpdateMap(each,each.getMonsterId());
        });
    }

    private List<EnemiesDB> getEnemiesDB() {

        return enemiesDbRepository.findAll();
    }

    private String getRewardId(String stageId){
        return stageId;
    }

    private void InitRewardsDB() {
        rewardsDbRepository.deleteAll();
    }

    private List<RewardsDB> getRewardsDB() {
        return rewardsDbRepository.findAll();
    }

    private void InitRaidBattleEffectGroupsDB(){
        RaidBattleEffectGroupsDB db1 = new RaidBattleEffectGroupsDB();
        db1.setEffectGroupId("1000");//自身普通攻击buff
        db1.setGroupOverlapping(1);//允许重叠
        db1.setGroupMaxStackValue(1000.0d);


        RaidBattleEffectGroupsDB db2 = new RaidBattleEffectGroupsDB();
        db2.setEffectGroupId("1001");//自身普通防御buff
        db2.setGroupOverlapping(0);//只取最大值
        db2.setGroupMaxStackValue(70.0d);

        RaidBattleEffectGroupsDB db3 = new RaidBattleEffectGroupsDB();
        db3.setEffectGroupId("1010");//自身普通攻击debuff
        db3.setGroupOverlapping(1);//允许重叠
        db3.setGroupMaxStackValue(50.0d);

        RaidBattleEffectGroupsDB db4 = new RaidBattleEffectGroupsDB();
        db4.setEffectGroupId("1011");//自身普通防御debuff
        db4.setGroupOverlapping(1);//允许重叠
        db4.setGroupMaxStackValue(50.0d);


        RaidBattleEffectGroupsDB db5 = new RaidBattleEffectGroupsDB();
        db5.setEffectGroupId("2000");//敌方普通攻击buff
        db5.setGroupOverlapping(1);//允许重叠
        db5.setGroupMaxStackValue(1000.0d);

        RaidBattleEffectGroupsDB db6 = new RaidBattleEffectGroupsDB();
        db6.setEffectGroupId("2001");//敌方普通防御buff
        db6.setGroupOverlapping(0);//只取最大值
        db6.setGroupMaxStackValue(70.0d);

        RaidBattleEffectGroupsDB db7 = new RaidBattleEffectGroupsDB();
        db7.setEffectGroupId("2010");//敌方普通攻击debuff
        db7.setGroupOverlapping(1);//允许重叠
        db7.setGroupMaxStackValue(50.0d);

        RaidBattleEffectGroupsDB db8 = new RaidBattleEffectGroupsDB();
        db8.setEffectGroupId("2011");//敌方普通防御debuff
        db8.setGroupOverlapping(1);//允许重叠
        db8.setGroupMaxStackValue(50.0d);

        List<RaidBattleEffectGroupsDB> list = Stream.of(db1,db2,db3,db4,db5,db6,db7,db8).collect(Collectors.toList());

        raidBattleEffectGroupsDbRepository.deleteAll();
        list.forEach(each->{
            raidBattleEffectGroupsDbRepository.deleteByEffectGroupId(each.getEffectGroupId());
            raidBattleEffectGroupsDbDao.saveOrUpdateMap(each,each.getEffectGroupId());
        });

    }

    private List<RaidBattleEffectGroupsDB> getRaidBattleEffectGroupsDB() {
        return raidBattleEffectGroupsDbRepository.findAll();
    }

    private void InitRaidBattleEffectsDB(){

        List<RaidBattleEffectGroupsDB> raidBattleEffectGroupsDB = getRaidBattleEffectGroupsDB();

        RaidBattleEffectGroupsDB group1000 = raidBattleEffectGroupsDB.stream().filter(each -> each.getEffectGroupId().equals("1000")).findFirst().get();

        RaidBattleEffectGroupsDB group2011 = raidBattleEffectGroupsDB.stream().filter(each -> each.getEffectGroupId().equals("2011")).findFirst().get();

        RaidBattleEffectsDB db1 = new RaidBattleEffectsDB();
        db1.setEffectId("10000");
        db1.setEffectGroup(group1000);
        db1.setEffectiveSecond(180);
        db1.setEffectProp(0);
        db1.setValue1(50);

        RaidBattleEffectsDB db2 = new RaidBattleEffectsDB();
        db2.setEffectId("10001");
        db2.setEffectGroup(group1000);
        db2.setEffectiveSecond(180);
        db2.setEffectProp(0);
        db2.setValue1(15);
        db2.setEffectMaxStack(5);

        RaidBattleEffectsDB db3 = new RaidBattleEffectsDB();
        db3.setEffectId("10003");
        db3.setEffectGroup(group1000);
        db3.setEffectiveTurn(6);
        db3.setEffectProp(0);
        db3.setValue1(25);
        db3.setEffectMaxStack(3);

        RaidBattleEffectsDB db4 = new RaidBattleEffectsDB();
        db4.setEffectId("10004");
        db4.setEffectGroup(group1000);
        db4.setEffectProp(0);
        db4.setValue1(10);
        db4.setEffectMaxStack(8);

        RaidBattleEffectsDB db16 = new RaidBattleEffectsDB();
        db16.setEffectId("10016");
        db16.setEffectGroup(group2011);
        db16.setEffectiveSecond(180);
        db16.setEffectProp(1);
        db16.setValue1(25);

        RaidBattleEffectsDB db17 = new RaidBattleEffectsDB();
        db17.setEffectId("10017");
        db17.setEffectGroup(group2011);
        db17.setEffectiveTurn(6);
        db17.setEffectProp(1);
        db17.setValue1(5);
        db17.setEffectMaxStack(4);

        RaidBattleEffectsDB db18 = new RaidBattleEffectsDB();
        db18.setEffectId("10018");
        db18.setEffectGroup(group2011);
        db18.setEffectiveTurn(6);
        db18.setEffectProp(1);
        db18.setValue1(5);
        db18.setEffectMaxStack(4);

        RaidBattleEffectsDB db19 = new RaidBattleEffectsDB();
        db19.setEffectId("10019");
        db19.setEffectGroup(group2011);
        db19.setEffectProp(1);
        db19.setValue1(10);
        db19.setEffectMaxStack(3);


        List<RaidBattleEffectsDB> list = new ArrayList<>();
        list.add(db1);
        list.add(db2);
        list.add(db3);
        list.add(db4);
        list.add(db16);
        list.add(db17);
        list.add(db18);
        list.add(db19);

        raidBattleEffectsDbRepository.deleteAll();

        list.forEach(each->{
            raidBattleEffectsDbRepository.deleteByEffectId(each.getEffectId());
            raidBattleEffectsDbDao.saveOrUpdateMap(each,each.getEffectId());
        });
    }

    private List<UltimateTypesDB> getUltimateTypesDB() {
        return ultimateTypesDbRepository.findAll();
    }

    private void InitUltimateTypesDB(){
        UltimateTypesDB db1 = new UltimateTypesDB();
        db1.setTypeId("0");
        db1.setType(0);
        db1.setName("无");

        UltimateTypesDB db2 = new UltimateTypesDB();
        db2.setTypeId("0");
        db2.setType(0);
        db2.setName("无");

        UltimateTypesDB db3 = new UltimateTypesDB();
        db3.setTypeId("0");
        db3.setType(0);
        db3.setName("无");

        UltimateTypesDB db4 = new UltimateTypesDB();
        db4.setTypeId("0");
        db4.setType(0);
        db4.setName("无");

        UltimateTypesDB db5 = new UltimateTypesDB();
        db5.setTypeId("0");
        db5.setType(0);
        db5.setName("无");

        UltimateTypesDB db6 = new UltimateTypesDB();
        db6.setTypeId("0");
        db6.setType(0);
        db6.setName("无");

        UltimateTypesDB db7 = new UltimateTypesDB();
        db7.setTypeId("0");
        db7.setType(0);
        db7.setName("无");

        List<UltimateTypesDB> list = Stream.of(db1, db2, db3, db4, db5, db6, db7).collect(Collectors.toList());

        ultimateTypesDbRepository.deleteAll();

        list.forEach(each->{
            ultimateTypesDbRepository.deleteById(each.getTypeId());
            ultimateTypesDbDao.saveOrUpdateMap(each,each.getTypeId());
        });

    }

    private List<CardSkillsDB> getCardSkillsDb(){
       return cardSkillsDbRepository.findAll();
    }

    private void InitCardSkillsDB(){
        CardSkillsDB db1 = new CardSkillsDB();
        db1.setSkillId("BaseSkill_Attack1");

        CardSkillsDB db2 = new CardSkillsDB();
        db2.setSkillId("BaseSkill_Attack2");

        CardSkillsDB db3 = new CardSkillsDB();
        db3.setSkillId("BaseSkill_HeavyAttack1");

        CardSkillsDB db4 = new CardSkillsDB();
        db4.setSkillId("BaseSkill_SeriesAttack1");

        CardSkillsDB db5 = new CardSkillsDB();
        db5.setSkillId("BaseSkill_ReduceDefenceAttack1");

        CardSkillsDB db6 = new CardSkillsDB();
        db6.setSkillId("BuffSkill_BuffAtk1");

        CardSkillsDB db7 = new CardSkillsDB();
        db7.setSkillId("BuffSkill_BuffAtk2");

        List<CardSkillsDB> list = Stream.of(db1, db2, db3, db4, db5, db6, db7).collect(Collectors.toList());
        cardSkillsDbRepository.deleteAll();

        list.forEach(each->{
            cardSkillsDbRepository.deleteById(each.getSkillId());
            cardSkillsDbDao.saveOrUpdateMap(each,each.getSkillId());
        });
    }

    private void InitCardsDb(){
        List<CardSkillsDB> cardSkillsDb = getCardSkillsDb();


        CardsDB db1 = new CardsDB();
        db1.setCardId("Card0001");
        db1.setCardSkill(cardSkillsDb.stream().filter(cardDb->cardDb.getSkillId().equals("BaseSkill_Attack1")).findFirst().get());
        db1.setName("攻击1");
        db1.setCost(10);
        db1.setLoad(50);
        db1.setValue1(110);

        CardsDB db2 = new CardsDB();
        db2.setCardId("Card0002");
        db2.setCardSkill(cardSkillsDb.stream().filter(cardDb->cardDb.getSkillId().equals("BaseSkill_Attack2")).findFirst().get());
        db2.setName("攻击2");
        db2.setCost(10);
        db2.setLoad(50);
        db2.setValue1(120);

        CardsDB db3 = new CardsDB();
        db3.setCardId("Card0003");
        db3.setCardSkill(cardSkillsDb.stream().filter(cardDb->cardDb.getSkillId().equals("BaseSkill_HeavyAttack1")).findFirst().get());
        db3.setName("重击1");
        db3.setCost(18);
        db3.setLoad(100);
        db3.setValue1(180);

        CardsDB db4 = new CardsDB();
        db4.setCardId("Card0003");
        db4.setCardSkill(cardSkillsDb.stream().filter(cardDb->cardDb.getSkillId().equals("BaseSkill_SeriesAttack1")).findFirst().get());
        db4.setName("连击1");
        db4.setCost(9);
        db4.setLoad(18);
        db4.setValue1(105);
        db4.setValue2(3);

    }
}
