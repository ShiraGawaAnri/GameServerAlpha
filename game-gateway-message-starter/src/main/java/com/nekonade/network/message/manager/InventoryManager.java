package com.nekonade.network.message.manager;


import com.nekonade.common.error.GameErrorException;
import com.nekonade.dao.db.entity.Inventory;
import com.nekonade.dao.db.entity.Item;
import com.nekonade.dao.db.entity.Weapon;
import lombok.Getter;

import java.util.concurrent.ConcurrentHashMap;

public class InventoryManager {

    @Getter
    private final Inventory inventory;
    
    public InventoryManager(Inventory inventory) {
        this.inventory = inventory;
    }

    public ConcurrentHashMap<String, Weapon> getWeaponMap(){
        return inventory.getWeaponMap();
    }
    public ConcurrentHashMap<String, Item> getItemMap(){
        return inventory.getItemMap();
    }
    public Weapon getWeapon(String weaponId) {
        return inventory.getWeaponMap().get(weaponId);
    }
    public void checkWeaponExist(String weaponId) {
        if(!this.inventory.getWeaponMap().containsKey(weaponId)) {
            throw GameErrorException.newBuilder(GameErrorCode.WeaponNotExist).build();
        }
    }
    public void checkWeaponHadEquiped(Weapon weapon) {
        if(!weapon.isEnable()) {
            throw GameErrorException.newBuilder(GameErrorCode.WeaponUnenable).build();
        }
    }
    public Item getItem(String itemId) {
        return inventory.getItemMap().get(itemId);
    }
    public void checkItemEnough(String propId,int needCount) {
        Item item = this.getItem(propId);
        if(item.getCount() < needCount) {
            throw GameErrorException.newBuilder(GameErrorCode.EquipWeaponCostNotEnough).message("需要{} {} ", item,needCount).build();
        }
    }
    
    public int consumeItem(String id, int count) {
        Item item = this.getItem(id);
        int value = item.getCount() - count;
        item.setCount(value);
        return value;
    }
}
