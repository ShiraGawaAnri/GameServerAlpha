package com.nekonade.neko.service.test;


import com.nekonade.common.dto.Item;
import com.nekonade.common.utils.FunctionMapper;
import com.nekonade.dao.daos.GlobalConfigDao;
import com.nekonade.dao.daos.ItemsDbDao;
import com.nekonade.dao.daos.RaidBattleDbDao;
import com.nekonade.dao.db.entity.MailBox;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.data.ItemsDB;
import com.nekonade.dao.db.entity.data.RaidBattleDB;
import com.nekonade.dao.db.repository.ItemsDbRepository;
import com.nekonade.dao.db.repository.MailBoxRepository;
import com.nekonade.dao.db.repository.PlayerRepository;
import com.nekonade.dao.db.repository.RaidBattleDbRepository;
import com.nekonade.neko.service.ItemDbService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TestDataInitService{

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

    @PostConstruct
    private void init(){
        Object[][] objects = ItemDbTestData();
        for (Object[] object : objects) {
            for (Object obj:object){
                ItemsDB item = (ItemsDB) obj;
                itemsDbRepository.deleteByItemId(item.getItemId());
                itemsDbDao.saveOrUpdateMap(item,Long.valueOf(item.getItemId()));
            }
        }
        List<MailBox> mailBoxes = new ArrayList<>();
        Random random = new Random();
        int times = random.nextInt(3) + 5;
        for(int i = 0;i < times;i++){
            mailBoxes.addAll(MailBoxTestData());
        }
        mailBoxes.forEach(each-> mailBoxRepository.save(each));
        //mailBoxRepository.saveAll(mailBoxes);

        List<RaidBattleDB> raidBattleDBS = RaidBattleTestData();
        raidBattleDBS.forEach(each->{
            String[] list = new String[]{
                    String.valueOf(each.getArea()),
                    String.valueOf(each.getEpisode()),
                    String.valueOf(each.getChapter()),
                    String.valueOf(each.getStage()),
                    String.valueOf(each.getDifficulty()),
            };
            String stageRedisKey = createStageRedisKey(list);
            raidBattleDbRepository.deleteByStageId(stageRedisKey);
            raidBattleDbDao.saveOrUpdateMap(each, stageRedisKey);
        });
    }

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
        return new Object[][] {
                { itemsDB1 }, { itemsDB2 }, { itemsDB3 },
                { itemsDB4 }, { itemsDB5 }
        };
    }

    @Test(dataProvider = "ItemDbTestData")
    private void LoadItemDb(ItemsDB itemsDB){
        itemDbService.addItemDb(itemsDB);
        ItemsDB op = itemDbService.findByItemId(itemsDB.getItemId());
        Assert.assertNotNull(op);
    }

    private List<MailBox> MailBoxTestData(){
        List<Player> all = playerRepository.findAll();
        Player player = all.get(0);
        long senderId = player.getPlayerId();
        String senderName = player.getNickName();
        List<ItemsDB> items = itemsDbRepository.findAll();
        return all.stream().skip(1).map(Player::getPlayerId).map(id -> {
            MailBox mailBox = new MailBox();
            mailBox.setSenderId(senderId);
            mailBox.setSenderName(senderName);
            mailBox.setTitle(DigestUtils.md5Hex(id + senderName + player + Math.random()));
            mailBox.setContent("Send To PlayerId:" + id);
            mailBox.setTimestamp(System.currentTimeMillis());
            mailBox.setExpired(Duration.ofDays(30).toMillis());
            mailBox.setReceiverId(id);
            Function<ItemsDB, Item> mapper = FunctionMapper.Mapper(ItemsDB.class, Item.class);
            Collections.shuffle(items);
            List<Item> list = items.stream().map(mapper).peek(each->{
                Random random = new Random();
                each.setCount(random.nextInt(10) + 1);
            }).collect(Collectors.toList());
            mailBox.setGifts(list);
            return mailBox;
        }).collect(Collectors.toList());
    }


    private String createStageRedisKey(String[] list){
        return "STAGE_" + String.join("_", Arrays.asList(list));
    }

    private List<RaidBattleDB> RaidBattleTestData(){
        RaidBattleDB raidBattleDB = new RaidBattleDB();
        raidBattleDB.setArea(1);
        raidBattleDB.setEpisode(1);
        raidBattleDB.setChapter(1);
        raidBattleDB.setStage(1);
        raidBattleDB.setDifficulty(1);
        raidBattleDB.setCostItem(false);
        raidBattleDB.setCostStaminaPoint(10);
        raidBattleDB.setMultiRaid(false);
        String[] r = new String[]{"1","1","1","1","1"};
        String rkey = createStageRedisKey(r);
        raidBattleDB.setStageId(rkey);

        RaidBattleDB raidBattleDB1 = new RaidBattleDB();
        raidBattleDB1.setArea(1);
        raidBattleDB1.setEpisode(1);
        raidBattleDB1.setChapter(1);
        raidBattleDB1.setStage(2);
        raidBattleDB1.setDifficulty(1);
        raidBattleDB1.setCostItem(true);
        raidBattleDB1.setCostItemId("5");
        raidBattleDB1.setCostItemCount(1);
        raidBattleDB1.setCostStaminaPoint(15);
        raidBattleDB1.setMultiRaid(false);
        String[] r1 = new String[]{"1","1","1","2","1"};
        String rkey1 = createStageRedisKey(r1);
        raidBattleDB1.setStageId(rkey1);
        List<RaidBattleDB> list = new ArrayList<>();
        list.add(raidBattleDB);
        list.add(raidBattleDB1);
        return list;
    }
}
