package com.nekonade.neko.service.test;


import com.nekonade.common.dto.ItemDTO;
import com.nekonade.common.utils.FunctionMapper;
import com.nekonade.dao.daos.*;
import com.nekonade.dao.db.entity.MailBox;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.data.EnemiesDB;
import com.nekonade.dao.db.entity.data.ItemsDB;
import com.nekonade.dao.db.entity.data.RaidBattleDB;
import com.nekonade.dao.db.entity.data.RewardsDB;
import com.nekonade.dao.db.repository.*;
import com.nekonade.neko.service.ItemDbService;
import com.nekonade.network.message.event.function.EnterGameEvent;
import com.nekonade.network.message.manager.InventoryManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
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
        return new Object[][]{
                {itemsDB1}, {itemsDB2}, {itemsDB3},
                {itemsDB4}, {itemsDB5}
        };
    }

    @EventListener
    public void loginAddExp(EnterGameEvent event) {
        event.getPlayerManager().getExperienceManager().addExperience(1000);
    }

    @EventListener
    public void addItem(EnterGameEvent event) {
        InventoryManager inventoryManager = event.getPlayerManager().getInventoryManager();
        List<ItemsDB> itemDbData = getItemDbData();
        itemDbData.forEach(item->{
            Random random = new Random();
            inventoryManager.produceItem(item.getItemId(), random.nextInt(20));
        });
    }

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

    @PostConstruct
    private void init() {
        InitItemsDB();
        InitEnemiesDB();
        InitRewardsDB();
        InitRaidBattleDB();

        SendMail();

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
        return all.stream().map(Player::getPlayerId).map(id -> {
            MailBox mailBox = new MailBox();
            mailBox.setSenderId(senderId);
            mailBox.setSenderName(senderName);
            mailBox.setTitle(DigestUtils.md5Hex(id + senderName + player + Math.random()));
            mailBox.setContent("Send To PlayerId:" + id);
            mailBox.setTimestamp(System.currentTimeMillis());
            mailBox.setExpired(Duration.ofDays(30).toMillis());
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
        raidBattleDB1.setLimitCounter(5);
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
        raidBattleDB2.setLimitCounter(10);
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

        List<RaidBattleDB> raidBattleDBS = new ArrayList<>();
        raidBattleDBS.add(raidBattleDB);
        raidBattleDBS.add(raidBattleDB1);
        raidBattleDBS.add(raidBattleDB2);

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
                rewardsDB.setRewardId(each.getStageId());
                item.setAmount(random.nextInt(5));
                item.setProb(random.nextDouble());
                Collections.shuffle(itemsDBS);
                item.setItemId(itemsDBS.get(0).getItemId());
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

        List<EnemiesDB> list = new ArrayList<>();
        list.add(enemiesDB);
        list.add(enemiesDB2);
        list.add(enemiesDB3);
        list.add(enemiesDB4);
        list.add(enemiesDB5);

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

/*        List<ItemsDB> itemsDBS = getItemDbData();
        List<RewardsDB> list = new ArrayList<>();
        Random randomTimes = new Random();
        int randTimes = randomTimes.nextInt(15) + 5;
        for(int count = 0; count < randTimes ;count++){
            RewardsDB rewardsDB = new RewardsDB();
            Random random = new Random();
            int rand = random.nextInt(8);
            for (int i = 0;i <= rand;i++){
                RewardsDB.Item item = new RewardsDB.Item();
                item.setAmount(random.nextInt(5));
                item.setProb(random.nextDouble());
                Collections.shuffle(itemsDBS);
                item.setItemId(itemsDBS.get(0).getItemId());
                rewardsDB.getItems().add(item);
            }
            rewardsDB.makeItem();
            list.add(rewardsDB);
        }
        list.forEach(each->{
            rewardsDbRepository.deleteByRewardId(each.getRewardId());
            rewardsDbDao.saveOrUpdateMap(each,each.getRewardId());
        });*/
        rewardsDbRepository.deleteAll();
    }

    private List<RewardsDB> getRewardsDB() {

        return rewardsDbRepository.findAll();
    }
}
