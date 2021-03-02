package com.nekonade.neko.service.test;


import com.nekonade.common.dto.ItemDTO;
import com.nekonade.common.utils.FunctionMapper;
import com.nekonade.dao.daos.*;
import com.nekonade.common.enums.EnumEntityDB;
import com.nekonade.dao.db.entity.MailBox;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.RaidBattleDirectiveEffect;
import com.nekonade.dao.db.entity.data.*;
import com.nekonade.dao.db.repository.*;
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

    @Autowired
    private CardsDbDao cardsDbDao;

    @Autowired
    private CardsDbRepository cardsDbRepository;

    @Autowired
    private CharactersDbDao charactersDbDao;

    @Autowired
    private CharactersDbRepository charactersDbRepository;

    @Autowired
    private GachaPoolsDbDao gachaPoolsDbDao;

    @Autowired
    private GachaPoolsDbRepository gachaPoolsDbRepository;

    @DataProvider(name = "ItemDbTestData")
    public static Object[][] ItemDbTestData() {
        ItemsDB itemsDB1 = new ItemsDB();
        itemsDB1.setItemId("1");
        itemsDB1.setName("Stamina回复药");
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
        itemsDB5.setName("珍贵素材A");
        itemsDB5.setCategory(2);
        itemsDB5.setType(2);

        ItemsDB itemsDB6 = new ItemsDB();
        itemsDB6.setItemId("6");
        itemsDB6.setName("碎片");
        itemsDB6.setCategory(2);
        itemsDB6.setType(2);

        ItemsDB itemsDB7 = new ItemsDB();
        itemsDB7.setItemId("1000");
        itemsDB7.setName("钻石");
        itemsDB7.setCategory(5);
        itemsDB7.setType(1);

        return new Object[][]{
                {itemsDB1}, {itemsDB2}, {itemsDB3},
                {itemsDB4}, {itemsDB5}, {itemsDB6},
                {itemsDB7}
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

    private void InitItemsDB() {
        Object[][] objects = ItemDbTestData();
        List<ItemsDB> itemsDBS = new ArrayList<>();
        for (Object[] object : objects) {
            for (Object obj : object) {
                ItemsDB item = (ItemsDB) obj;
                itemsDBS.add(item);
            }
        }
        itemsDBS.forEach(item -> {
            itemsDbRepository.deleteByItemId(item.getItemId());
            itemsDbDao.saveOrUpdateMap(item, item.getItemId());
        });
    }

    private List<ItemsDB> getItemDbData() {
        return itemsDbRepository.findAll();
    }

    private ScheduledFuture<?> scheduledFuture;
    private final DefaultEventExecutor eventExecutors = new DefaultEventExecutor();

    @PostConstruct
    private void init() {

        scheduledFuture = eventExecutors.scheduleWithFixedDelay(() -> {
            try {
                InitItemsDB();
                InitEnemiesDB();
                InitRewardsDB();
                InitRaidBattleDB();
                SendMail();
                InitRaidBattleEffectGroupsDB();
                InitRaidBattleEffectsDB();
                InitUltimateTypesDB();
                InitCardSkillsDB();
                InitCardsDb();
                InitCharactersDb();
                InitGachaPoolsDb();

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (scheduledFuture != null) {
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

    private void InitRaidBattleDB() {
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
            if (test_monster_0001.isPresent()) {
                enemyIds.add("TEST_MONSTER_0001");
                raidBattleDB.getEnemyList().add(test_monster_0001.get());
            }

            Optional<EnemiesDB> test_monster_0002 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0002")).findFirst();
            if (test_monster_0002.isPresent()) {
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
        raidBattleDB1.setLimitCounterRefreshType(EnumEntityDB.EnumNumber.RaidBattle_Create_LimitCounterRefreshType_None.getValue());
        String[] r1 = new String[]{"1", "1", "1", "2", "1"};
        String rkey1 = createStageRedisKey(r1);
        raidBattleDB1.setStageId(rkey1);
        {
            CopyOnWriteArrayList<String> enemyIds = new CopyOnWriteArrayList<>();

            Optional<EnemiesDB> test_monster_0003 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0003")).findFirst();
            if (test_monster_0003.isPresent()) {
                enemyIds.add("TEST_MONSTER_0003");
                raidBattleDB1.getEnemyList().add(test_monster_0003.get());
            }

            Optional<EnemiesDB> test_monster_0004 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0004")).findFirst();
            if (test_monster_0004.isPresent()) {
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
        raidBattleDB2.setLimitCounterRefreshType(EnumEntityDB.EnumNumber.Week_Tuesday.getValue());
        String[] r2 = new String[]{"1", "1", "1", "3", "1"};
        String rkey2 = createStageRedisKey(r2);
        raidBattleDB2.setStageId(rkey2);

        {
            CopyOnWriteArrayList<String> enemyIds = new CopyOnWriteArrayList<>();

            Optional<EnemiesDB> test_monster_0005 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0005")).findFirst();
            if (test_monster_0005.isPresent()) {
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
        raidBattleDB3.setLimitCounterRefreshType(EnumEntityDB.EnumNumber.Week_Saturday.getValue());
        String[] r3 = new String[]{"1", "1", "1", "4", "1"};
        String rkey3 = createStageRedisKey(r3);
        raidBattleDB3.setStageId(rkey3);

        {
            CopyOnWriteArrayList<String> enemyIds = new CopyOnWriteArrayList<>();

            Optional<EnemiesDB> test_monster_0005 = enemiesDBS.stream().filter(each -> each.getMonsterId().equals("TEST_MONSTER_0005")).findFirst();
            if (test_monster_0005.isPresent()) {
                enemyIds.add("TEST_MONSTER_0005");
                raidBattleDB3.getEnemyList().add(test_monster_0005.get());
            }
            raidBattleDB3.setEnemyIds(enemyIds);
        }

        List<RaidBattleDB> raidBattleDBS = Stream.of(raidBattleDB, raidBattleDB2, raidBattleDB3).collect(Collectors.toList());

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
            for (int i = 0; i <= rand; i++) {
                RewardsDB.Item item = new RewardsDB.Item();
                BeanUtils.copyProperties(itemsDBS.get(0), item);
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
            rewardsDbDao.saveOrUpdate(rewardsDB, rewardsDB.getRewardId());
        });
    }

    private List<RaidBattleDB> getRaidBattleDB() {

        return raidBattleDbRepository.findAll();
    }

    private void InitEnemiesDB() {
        EnemiesDB enemiesDB = new EnemiesDB();
        enemiesDB.setMonsterId("TEST_MONSTER_0001");
        enemiesDB.setName("测试怪物1");
        enemiesDB.setHp(100L);

        EnemiesDB enemiesDB2 = new EnemiesDB();
        enemiesDB2.setMonsterId("TEST_MONSTER_0002");
        enemiesDB2.setName("测试怪物2");
        enemiesDB2.setHp(1500L);


        EnemiesDB enemiesDB3 = new EnemiesDB();
        enemiesDB3.setMonsterId("TEST_MONSTER_0003");
        enemiesDB3.setName("测试怪物3");
        enemiesDB3.setHp(1200L);

        EnemiesDB enemiesDB4 = new EnemiesDB();
        enemiesDB4.setMonsterId("TEST_MONSTER_0004");
        enemiesDB4.setName("测试怪物4");
        enemiesDB4.setHp(10000L);
        enemiesDB4.setKey(1);

        EnemiesDB enemiesDB5 = new EnemiesDB();
        enemiesDB5.setMonsterId("TEST_MONSTER_0005");
        enemiesDB5.setName("测试怪物5");
        enemiesDB5.setHp(5000L);

        List<EnemiesDB> list = Stream.of(enemiesDB, enemiesDB2, enemiesDB3, enemiesDB4, enemiesDB5).collect(Collectors.toList());

        list.forEach(each -> {
            enemiesDbRepository.deleteByMonsterId(each.getMonsterId());
            enemiesDbDao.saveOrUpdateMap(each, each.getMonsterId());
        });
    }

    private List<EnemiesDB> getEnemiesDB() {

        return enemiesDbRepository.findAll();
    }

    private String getRewardId(String stageId) {
        return stageId;
    }

    private void InitRewardsDB() {
        rewardsDbRepository.deleteAll();
    }

    private List<RewardsDB> getRewardsDB() {
        return rewardsDbRepository.findAll();
    }

    private void InitRaidBattleEffectGroupsDB() {
        RaidBattleEffectGroupsDB db1 = new RaidBattleEffectGroupsDB();
        db1.setEffectGroupId("1000");//普通攻击buff
        db1.setGroupOverlapping(1);//允许重叠
        db1.setGroupMaxStackValue(1000.0d);


        RaidBattleEffectGroupsDB db2 = new RaidBattleEffectGroupsDB();
        db2.setEffectGroupId("1001");//普通防御buff
        db2.setGroupOverlapping(0);//只取最大值
        db2.setGroupMaxStackValue(70.0d);

        RaidBattleEffectGroupsDB db3 = new RaidBattleEffectGroupsDB();
        db3.setEffectGroupId("1010");//普通攻击debuff
        db3.setGroupOverlapping(1);//允许重叠
        db3.setGroupMaxStackValue(50.0d);

        RaidBattleEffectGroupsDB db4 = new RaidBattleEffectGroupsDB();
        db4.setEffectGroupId("1011");//普通防御debuff
        db4.setGroupOverlapping(1);//允许重叠
        db4.setGroupMaxStackValue(50.0d);

        RaidBattleEffectGroupsDB db6 = new RaidBattleEffectGroupsDB();
        db6.setEffectGroupId("1013");//特殊防御debuff
        db6.setGroupOverlapping(1);//允许重叠
        db6.setGroupMaxStackValue(10.0d);


        List<RaidBattleEffectGroupsDB> list = Stream.of(db1, db2, db3, db4, db6).collect(Collectors.toList());

        raidBattleEffectGroupsDbRepository.deleteAll();
        list.forEach(each -> {
            raidBattleEffectGroupsDbRepository.deleteByEffectGroupId(each.getEffectGroupId());
            raidBattleEffectGroupsDbDao.saveOrUpdateMap(each, each.getEffectGroupId());
        });

    }

    private List<RaidBattleEffectGroupsDB> getRaidBattleEffectGroupsDB() {
        return raidBattleEffectGroupsDbRepository.findAll();
    }

    private List<RaidBattleEffectsDB> getRaidBattleEffectsDB() {
        return raidBattleEffectsDbRepository.findAll();
    }

    private void InitRaidBattleEffectsDB() {

        List<RaidBattleEffectGroupsDB> raidBattleEffectGroupsDB = getRaidBattleEffectGroupsDB();

        RaidBattleEffectGroupsDB group1000 = raidBattleEffectGroupsDB.stream().filter(each -> each.getEffectGroupId().equals("1000")).findFirst().get();

        RaidBattleEffectGroupsDB group1001 = raidBattleEffectGroupsDB.stream().filter(each -> each.getEffectGroupId().equals("1001")).findFirst().get();

        RaidBattleEffectGroupsDB group1010 = raidBattleEffectGroupsDB.stream().filter(each -> each.getEffectGroupId().equals("1010")).findFirst().get();

        RaidBattleEffectGroupsDB group1011 = raidBattleEffectGroupsDB.stream().filter(each -> each.getEffectGroupId().equals("1011")).findFirst().get();

        /*RaidBattleEffectGroupsDB group2011 = raidBattleEffectGroupsDB.stream().filter(each -> each.getEffectGroupId().equals("2011")).findFirst().get();*/

        int buffPropValue = EnumEntityDB.EnumNumber.RaidBattle_Effect_Prop_Buff.getValue();
        int debuffPropValue = EnumEntityDB.EnumNumber.RaidBattle_Effect_Prop_Debuff.getValue();

        RaidBattleEffectsDB db1 = new RaidBattleEffectsDB();
        db1.setEffectId("Buff_Atk1");
        db1.setEffectGroup(group1000);
        db1.setEffectiveSecond(180);
        db1.setEffectProp(buffPropValue);
        db1.setValue1(50);

        RaidBattleEffectsDB db2 = new RaidBattleEffectsDB();
        db2.setEffectId("Buff_Atk2");
        db2.setEffectGroup(group1000);
        db2.setEffectiveSecond(180);
        db2.setEffectProp(buffPropValue);
        db2.setValue1(15);
        db2.setEffectMaxStack(5);

        RaidBattleEffectsDB db3 = new RaidBattleEffectsDB();
        db3.setEffectId("Buff_Atk3");
        db3.setEffectGroup(group1000);
        db3.setEffectiveTurn(6);
        db3.setEffectProp(buffPropValue);
        db3.setValue1(25);
        db3.setEffectMaxStack(3);

        RaidBattleEffectsDB db4 = new RaidBattleEffectsDB();
        db4.setEffectId("Buff_Atk4");
        db4.setEffectGroup(group1000);
        db4.setEffectProp(buffPropValue);
        db4.setValue1(10);
        db4.setEffectMaxStack(8);

        RaidBattleEffectsDB db5 = new RaidBattleEffectsDB();
        db5.setEffectId("Buff_Def1");
        db5.setEffectGroup(group1001);
        db5.setEffectiveSecond(180);
        db5.setEffectProp(buffPropValue);
        db5.setValue1(20);

        RaidBattleEffectsDB db6 = new RaidBattleEffectsDB();
        db6.setEffectId("Buff_Def2");
        db6.setEffectGroup(group1001);
        db6.setEffectiveSecond(60);
        db6.setEffectProp(buffPropValue);
        db6.setValue1(50);

        RaidBattleEffectsDB db7 = new RaidBattleEffectsDB();
        db7.setEffectId("Buff_Def3");
        db7.setEffectGroup(group1001);
        db7.setEffectiveTurn(2);
        db7.setEffectProp(buffPropValue);
        db7.setValue1(70);


        RaidBattleEffectsDB db16 = new RaidBattleEffectsDB();
        db16.setEffectId("Debuff_Def1");
        db16.setEffectGroup(group1011);
        db16.setEffectiveSecond(180);
        db16.setEffectProp(debuffPropValue);
        db16.setValue1(25);

        RaidBattleEffectsDB db17 = new RaidBattleEffectsDB();
        db17.setEffectId("Debuff_Def2");
        db17.setEffectGroup(group1011);
        db17.setEffectiveTurn(6);
        db17.setEffectProp(debuffPropValue);
        db17.setValue1(5);
        db17.setEffectMaxStack(4);

        RaidBattleEffectsDB db18 = new RaidBattleEffectsDB();
        db18.setEffectId("Debuff_Def3");
        db18.setEffectGroup(group1011);
        db18.setEffectiveTurn(6);
        db18.setEffectProp(debuffPropValue);
        db18.setValue1(5);
        db18.setEffectMaxStack(4);

        RaidBattleEffectsDB db19 = new RaidBattleEffectsDB();
        db19.setEffectId("Debuff_Def4");
        db19.setEffectGroup(group1011);
        db19.setEffectProp(debuffPropValue);
        db19.setValue1(10);
        db19.setEffectMaxStack(3);


        List<RaidBattleEffectsDB> list = new ArrayList<>();
        list.add(db1);
        list.add(db2);
        list.add(db3);
        list.add(db4);
        list.add(db5);
        list.add(db6);
        list.add(db7);
        list.add(db16);
        list.add(db17);
        list.add(db18);
        list.add(db19);

        raidBattleEffectsDbRepository.deleteAll();

        list.forEach(each -> {
            raidBattleEffectsDbRepository.deleteByEffectId(each.getEffectId());
            raidBattleEffectsDbDao.saveOrUpdateMap(each, each.getEffectId());
        });
    }

    private List<UltimateTypesDB> getUltimateTypesDB() {
        return ultimateTypesDbRepository.findAll();
    }

    private void InitUltimateTypesDB() {
        UltimateTypesDB db1 = new UltimateTypesDB();
        db1.setTypeId("1000001");
        db1.setType(0);
        db1.setName("无");

        UltimateTypesDB db2 = new UltimateTypesDB();
        db2.setTypeId("1000002");
        db2.setType(1);
        db2.setName("动能");

        UltimateTypesDB db3 = new UltimateTypesDB();
        db3.setTypeId("1000003");
        db3.setType(2);
        db3.setName("热量");

        UltimateTypesDB db4 = new UltimateTypesDB();
        db4.setTypeId("1000004");
        db4.setType(3);
        db4.setName("电击");

        UltimateTypesDB db5 = new UltimateTypesDB();
        db5.setTypeId("1000005");
        db5.setType(4);
        db5.setName("爆炸");

        UltimateTypesDB db6 = new UltimateTypesDB();
        db6.setTypeId("1000006");
        db6.setType(5);
        db6.setName("辐射");

        List<UltimateTypesDB> list = Stream.of(db1, db2, db3, db4, db5, db6).collect(Collectors.toList());

        ultimateTypesDbRepository.deleteAll();

        list.forEach(each -> {
            ultimateTypesDbRepository.deleteById(each.getTypeId());
            ultimateTypesDbDao.saveOrUpdateMap(each, each.getTypeId());
        });

    }

    private List<ActiveSkillsDB> getCardSkillsDb() {
        return cardSkillsDbRepository.findAll();
    }

    private void InitCardSkillsDB() {

        ActiveSkillsDB db0 = new ActiveSkillsDB();
        db0.setId("BaseSkill_NoAction");//多为纯粹施放buff/debuff/场地效果


        ActiveSkillsDB db1 = new ActiveSkillsDB();
        db1.setId("BaseSkill_Attack1");

        ActiveSkillsDB db2 = new ActiveSkillsDB();
        db2.setId("BaseSkill_Attack2");

        ActiveSkillsDB db3 = new ActiveSkillsDB();
        db3.setId("BaseSkill_HeavyAttack1");

        ActiveSkillsDB db4 = new ActiveSkillsDB();
        db4.setId("BaseSkill_SeriesAttack1");

        ActiveSkillsDB db5 = new ActiveSkillsDB();
        db5.setId("BaseSkill_ReduceDefenceAttack1");

        ActiveSkillsDB db6 = new ActiveSkillsDB();
        db6.setId("BuffSkill_BuffAtk1");

        ActiveSkillsDB db7 = new ActiveSkillsDB();
        db7.setId("BuffSkill_BuffAtk2");

        List<ActiveSkillsDB> list = Stream.of(db0, db1, db2, db3, db4, db5, db6, db7).collect(Collectors.toList());
        cardSkillsDbRepository.deleteAll();

        list.forEach(each -> {
            cardSkillsDbRepository.deleteById(each.getId());
            cardSkillsDbDao.saveOrUpdateMap(each, each.getId());
        });
    }

    private void InitCardsDb() {


        List<ActiveSkillsDB> activeSkillsDb = getCardSkillsDb();

        List<RaidBattleEffectsDB> raidBattleEffectsDB = getRaidBattleEffectsDB();

        String targetToEnemyValue = EnumEntityDB.EnumString.RaidBattle_Effect_TargetTo_Enemy.getValue();
        String targetToPlayerValue = EnumEntityDB.EnumString.RaidBattle_Effect_TargetTo_Player.getValue();


        CardsDB db1 = new CardsDB();
        db1.setCardId("Card0001");
        db1.setCardSkill(activeSkillsDb.stream().filter(cardDb -> cardDb.getId().equals("BaseSkill_Attack1")).findFirst().get());
        db1.setName("攻击1");
        db1.setCost(10);
        db1.setLoad(50);
        db1.setValue1(110);

        CardsDB db2 = new CardsDB();
        db2.setCardId("Card0002");
        db2.setCardSkill(activeSkillsDb.stream().filter(cardDb -> cardDb.getId().equals("BaseSkill_Attack2")).findFirst().get());
        db2.setName("攻击2");
        db2.setCost(10);
        db2.setLoad(50);
        db2.setValue1(120);

        CardsDB db3 = new CardsDB();
        db3.setCardId("Card0003");
        db3.setCardSkill(activeSkillsDb.stream().filter(cardDb -> cardDb.getId().equals("BaseSkill_HeavyAttack1")).findFirst().get());
        db3.setName("重击1");
        db3.setCost(18);
        db3.setLoad(100);
        db3.setValue1(180);

        CardsDB db4 = new CardsDB();
        db4.setCardId("Card0004");
        db4.setCardSkill(activeSkillsDb.stream().filter(cardDb -> cardDb.getId().equals("BaseSkill_SeriesAttack1")).findFirst().get());
        db4.setName("连击1");
        db4.setCost(9);
        db4.setLoad(50);
        db4.setValue1(50);

        CardsDB db5 = new CardsDB();
        db5.setCardId("Card0005");
        db5.setCardSkill(activeSkillsDb.stream().filter(cardDb -> cardDb.getId().equals("BaseSkill_SeriesAttack1")).findFirst().get());
        db5.setName("连续攻击1-附带减防1-默认效果");
        db5.setCost(15);
        db5.setLoad(18);
        db5.setValue1(25);
        RaidBattleDirectiveEffect effect5_1 = new RaidBattleDirectiveEffect();
        effect5_1.setTargetTo(targetToEnemyValue);
        effect5_1.setEffect(raidBattleEffectsDB.stream().filter(effectDb -> effectDb.getEffectId().equals("Debuff_Def1")).findFirst().get());
        db5.setEffects(Collections.singletonList(effect5_1));

        CardsDB db6 = new CardsDB();
        db6.setCardId("Card0006");
        db6.setCardSkill(activeSkillsDb.stream().filter(cardDb -> cardDb.getId().equals("BaseSkill_HeavyAttack1")).findFirst().get());
        db6.setName("重击2-附带减防2-自定义效果量50");
        db6.setCost(22);
        db6.setLoad(100);
        db6.setValue1(150);
        RaidBattleDirectiveEffect effect6_1 = new RaidBattleDirectiveEffect();
        //动态效果量
        RaidBattleEffectsDB effect6_debuff_def2 = raidBattleEffectsDB.stream().filter(effectDb -> effectDb.getEffectId().equals("Debuff_Def2")).findFirst().get();
        effect6_debuff_def2.setValue1(12);
        effect6_1.setTargetTo(targetToEnemyValue);
        effect6_1.setEffect(effect6_debuff_def2);
        db6.setEffects(Collections.singletonList(effect6_1));

        CardsDB db7 = new CardsDB();
        db7.setCardId("Card0007");
        db7.setCardSkill(activeSkillsDb.stream().filter(cardDb -> cardDb.getId().equals("BaseSkill_ReduceDefenceAttack1")).findFirst().get());
        db7.setName("减防攻击-附带减防3-自定义效果");
        db7.setCost(5);
        db7.setLoad(25);
        db7.setValue1(30);
        RaidBattleDirectiveEffect effect7_1 = new RaidBattleDirectiveEffect();
        //动态效果量
        RaidBattleEffectsDB effect7_debuff_def4 = raidBattleEffectsDB.stream().filter(effectDb -> effectDb.getEffectId().equals("Debuff_Def4")).findFirst().get();
        effect7_debuff_def4.setValue1(10);
        effect7_1.setTargetTo(targetToEnemyValue);
        effect7_1.setEffect(effect7_debuff_def4);
        db7.setEffects(Collections.singletonList(effect7_1));

        CardsDB db8 = new CardsDB();
        db8.setCardId("Card0008");
        db8.setCardSkill(activeSkillsDb.stream().filter(cardDb -> cardDb.getId().equals("BaseSkill_NoAction")).findFirst().get());
        db8.setName("附加功防双Buff");
        db8.setCost(20);
        db8.setLoad(75);
        RaidBattleDirectiveEffect effect8_1 = new RaidBattleDirectiveEffect();
        RaidBattleEffectsDB effect8_buff_atk3 = raidBattleEffectsDB.stream().filter(effectDb -> effectDb.getEffectId().equals("Buff_Atk2")).findFirst().get();
        effect8_1.setTargetTo(targetToPlayerValue);
        effect8_1.setEffect(effect8_buff_atk3);


        RaidBattleDirectiveEffect effect8_2 = new RaidBattleDirectiveEffect();
        RaidBattleEffectsDB effect8_buff_def2 = raidBattleEffectsDB.stream().filter(effectDb -> effectDb.getEffectId().equals("Buff_Def2")).findFirst().get();
        effect8_2.setTargetTo(targetToPlayerValue);
        effect8_2.setEffect(effect8_buff_def2);

        db8.setEffects(Stream.of(effect8_1, effect8_2).collect(Collectors.toList()));


        List<CardsDB> list = Stream.of(db1, db2, db3, db4, db5, db6, db7, db8).collect(Collectors.toList());

        cardsDbRepository.deleteAll();

        list.forEach(each -> {
            cardsDbRepository.deleteById(each.getCardId());
            cardsDbDao.saveOrUpdateMap(each, each.getCardId());
        });
    }

    private List<CharactersDB> getCharactersDb() {
        return charactersDbRepository.findAll();
    }

    private void InitCharactersDb() {

        List<UltimateTypesDB> ultimateTypesDB = getUltimateTypesDB();

        UltimateTypesDB ultimateTypesDB_1 = ultimateTypesDB.stream().filter(db -> db.getType() == 1).findFirst().get();

        UltimateTypesDB ultimateTypesDB_2 = ultimateTypesDB.stream().filter(db -> db.getType() == 2).findFirst().get();

        UltimateTypesDB ultimateTypesDB_3 = ultimateTypesDB.stream().filter(db -> db.getType() == 3).findFirst().get();

        UltimateTypesDB ultimateTypesDB_4 = ultimateTypesDB.stream().filter(db -> db.getType() == 4).findFirst().get();


        CharactersDB db1 = new CharactersDB();
        db1.setCharaId("TEST_CHARA_0001");
        db1.setName("测试角色1001");
        db1.setUltimateType(ultimateTypesDB_1);
        db1.setBaseHp(250);
        db1.setBaseCost(10);
        db1.setBaseGuard(100);
        db1.setBaseSpeed(100d);

        CharactersDB db2 = new CharactersDB();
        db2.setCharaId("TEST_CHARA_0002");
        db2.setName("测试角色1002");
        db2.setUltimateType(ultimateTypesDB_2);
        db2.setBaseHp(600);
        db2.setBaseCost(18);
        db2.setBaseGuard(100);
        db2.setBaseSpeed(100d);

        CharactersDB db3 = new CharactersDB();
        db3.setCharaId("TEST_CHARA_0003");
        db3.setName("测试角色1003");
        db3.setUltimateType(ultimateTypesDB_3);
        db3.setBaseHp(150);
        db3.setBaseCost(15);
        db3.setBaseGuard(100);
        db3.setBaseSpeed(100d);

        CharactersDB db4 = new CharactersDB();
        db4.setCharaId("TEST_CHARA_0004");
        db4.setName("测试角色1004");
        db4.setUltimateType(ultimateTypesDB_4);
        db4.setBaseHp(150);
        db4.setBaseCost(13);
        db4.setBaseGuard(100);
        db4.setBaseSpeed(100d);

        List<CharactersDB> list = Stream.of(db1, db2, db3, db4).collect(Collectors.toList());

        charactersDbRepository.deleteAll();

        list.forEach(each -> {
            charactersDbRepository.deleteById(each.getCharaId());
            charactersDbDao.saveOrUpdateMap(each, each.getCharaId());
        });
    }

    private void InitGachaPoolsDb() {

        List<CharactersDB> charactersDb = getCharactersDb();

        CharactersDB test_chara_0001 = charactersDb.stream().filter(each -> each.getCharaId().equals("TEST_CHARA_0001")).findFirst().get();
        CharactersDB test_chara_0002 = charactersDb.stream().filter(each -> each.getCharaId().equals("TEST_CHARA_0002")).findFirst().get();
        CharactersDB test_chara_0003 = charactersDb.stream().filter(each -> each.getCharaId().equals("TEST_CHARA_0003")).findFirst().get();
        CharactersDB test_chara_0004 = charactersDb.stream().filter(each -> each.getCharaId().equals("TEST_CHARA_0004")).findFirst().get();


        GachaPoolsDB db1 = new GachaPoolsDB();
        db1.setGachaPoolId("GachaPoolAlpha0001");
        db1.setActive(true);

        GachaPoolsDB.Character chara1 = new GachaPoolsDB.Character();
        chara1.setCharaId(test_chara_0001.getCharaId());
        chara1.setProb(0.15);

        GachaPoolsDB.Character chara2 = new GachaPoolsDB.Character();
        chara2.setCharaId(test_chara_0002.getCharaId());
        chara2.setProb(0.4);

        GachaPoolsDB.Character chara3 = new GachaPoolsDB.Character();
        chara3.setCharaId(test_chara_0003.getCharaId());
        chara3.setProb(0.25);

        GachaPoolsDB.Character chara4 = new GachaPoolsDB.Character();
        chara4.setCharaId(test_chara_0004.getCharaId());
        chara4.setProb(0.2);

        List<GachaPoolsDB.Character> list = Stream.of(chara1, chara2, chara3, chara4).collect(Collectors.toList());

        db1.setCharacters(list);

        gachaPoolsDbRepository.deleteAll();

        gachaPoolsDbRepository.deleteById(db1.getGachaPoolId());
        gachaPoolsDbDao.saveOrUpdateMap(db1, db1.getGachaPoolId());

    }


}
