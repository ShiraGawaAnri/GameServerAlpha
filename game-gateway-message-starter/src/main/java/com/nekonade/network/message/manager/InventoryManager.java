package com.nekonade.network.message.manager;


import com.nekonade.common.error.GameNotifyException;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.dao.daos.ItemsDbDao;
import com.nekonade.dao.db.entity.Inventory;
import com.nekonade.dao.db.entity.Item;
import com.nekonade.dao.db.entity.Weapon;
import com.nekonade.dao.db.entity.data.ItemsDB;
import com.nekonade.network.message.event.function.ItemAddEvent;
import com.nekonade.network.message.event.function.ItemSubEvent;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InventoryManager {

    private final PlayerManager playerManager;

    private final ApplicationContext context;

    private final ItemsDbDao itemsDbDao;

    @Getter
    private final Inventory inventory;

    public InventoryManager(PlayerManager playerManager) {
        this.context = playerManager.getContext();
        this.playerManager = playerManager;
        this.inventory = playerManager.getPlayer().getInventory();
        this.itemsDbDao = context.getBean(ItemsDbDao.class);
    }

    public ConcurrentHashMap<String, Weapon> getWeaponMap() {
        return inventory.getWeaponMap();
    }

    public ConcurrentHashMap<String, Item> getItemMap() {
        return inventory.getItemMap();
    }

    public Weapon getWeapon(String weaponId) {
        return inventory.getWeaponMap().get(weaponId);
    }

    public void checkWeaponExist(String weaponId) {
        if (!this.inventory.getWeaponMap().containsKey(weaponId)) {
            throw GameNotifyException.newBuilder(GameErrorCode.WeaponNotExist).build();
        }
    }

    public void checkWeaponHadEquipped(Weapon weapon) {
        if (!weapon.isEnable()) {
            throw GameNotifyException.newBuilder(GameErrorCode.WeaponUnenable).build();
        }
    }

    public Item getItem(String itemId) {
        return inventory.getItemMap().get(itemId);
    }

    public ItemsDB getItemDb(String itemId){
        Query query = new Query(Criteria.where("itemId").is(itemId));
        return itemsDbDao.findByIdInMap(query, itemId, ItemsDB.class);
    }

    public boolean checkItemExist(String itemId){
        return getItemDb(itemId) != null;
    }

    public void checkItemEnough(String itemId, int needCount) {
        Map<String, Integer> costMap = new HashMap<>();
        costMap.put(itemId, needCount);
        this.checkItemEnough(costMap);
    }

    public boolean checkItemEnough(Map<String, Integer> costMap) {
        Set<Map<String, Integer>> collect = costMap.keySet().stream().map(itemId -> {
            Integer needCount = costMap.get(itemId);
            Item item = this.getItem(itemId);
            if (item == null || item.getAmount() < needCount) {
                Map<String, Integer> map = new HashMap<>();
                map.put(itemId, (item == null ? needCount : (item.getAmount() == null ? needCount : needCount - item.getAmount())));
                return map;
            }
            return null;
        }).collect(Collectors.toSet());
        collect.removeAll(Collections.singletonList(null));
        if (collect.size() > 0) {
            throw GameNotifyException.newBuilder(GameErrorCode.StageCostItemNotEnough).data(collect).build();
        }
        return true;
    }

    public boolean checkOverFlow(String itemId,int addValue){

        ItemsDB itemDb = getItemDb(itemId);
        if(itemDb == null){
            return true;
        }

        Item item = this.getItem(itemId);
        if(item == null){
            return false;
        }
        int count = item.getAmount() == null ? 0 : item.getAmount();

        long maxStack = itemDb.getMaxStack();

        return (count + addValue) > maxStack;
    }

    public boolean produceItem(String itemId, int amount) {
        if(checkOverFlow(itemId,amount)) return false;
        ItemAddEvent itemAddEvent = new ItemAddEvent(this, playerManager, itemId, amount);
        context.publishEvent(itemAddEvent);
        return true;
    }

    public void consumeItem(Map<String, Integer> costMap) {
        costMap.keySet().forEach(each -> {
            this.consumeItem(each, costMap.get(each));
        });
    }

    public void consumeItem(String itemId, int count) {
        ItemSubEvent itemSubEvent = new ItemSubEvent(this, playerManager, itemId, count);
        context.publishEvent(itemSubEvent);
    }
}
