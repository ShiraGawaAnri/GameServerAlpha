package com.nekonade.network.message.manager;


import com.nekonade.common.error.GameNotification;
import com.nekonade.dao.db.entity.Inventory;
import com.nekonade.dao.db.entity.Item;
import com.nekonade.dao.db.entity.Weapon;
import com.nekonade.network.message.event.function.ItemAddEvent;
import com.nekonade.network.message.event.function.ItemSubEvent;
import lombok.Getter;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InventoryManager {

    private final PlayerManager playerManager;

    private final ApplicationContext context;

    @Getter
    private final Inventory inventory;

    public InventoryManager(PlayerManager playerManager) {
        this.context = playerManager.getContext();
        this.playerManager = playerManager;
        this.inventory = playerManager.getPlayer().getInventory();
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
            throw GameNotification.newBuilder(GameErrorCode.WeaponNotExist).build();
        }
    }

    public void checkWeaponHadEquiped(Weapon weapon) {
        if (!weapon.isEnable()) {
            throw GameNotification.newBuilder(GameErrorCode.WeaponUnenable).build();
        }
    }

    public Item getItem(String itemId) {
        return inventory.getItemMap().get(itemId);
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
            if (item == null || item.getCount() < needCount) {
                Map<String, Integer> map = new HashMap<>();
                map.put(itemId, item == null ? needCount : needCount - item.getCount());
                return map;
            }
            return null;
        }).collect(Collectors.toSet());
        collect.remove(null);
        if (collect.size() > 0) {
            throw GameNotification.newBuilder(GameErrorCode.StageCostItemNotEnough).data(collect).build();
        }
        return true;
    }

    public void produceItem(String itemId, int count) {
        ItemAddEvent itemAddEvent = new ItemAddEvent(this, playerManager, itemId, count);
        context.publishEvent(itemAddEvent);
    }

    public void consumeItem(Map<String, Integer> costMap) {
        costMap.keySet().forEach(each -> {
            this.consumeItem(each, costMap.get(each));
        });
    }

    public void consumeItem(String itemId, int count) {
        ItemSubEvent itemSubEvent = new ItemSubEvent(this, playerManager, itemId, count);
        context.publishEvent(itemSubEvent);
//        Item item = inventory.getItemMap().get(itemId);
//        if(item == null){
//            return;
//        }
//        item.setCount(Math.max(0,item.getCount() - count));
    }


//    public int consumeItem(String id, int count) {
//        Item item = this.getItem(id);
//        int value = item.getCount() - count;
//        item.setCount(value);
//        return value;
//    }
}
