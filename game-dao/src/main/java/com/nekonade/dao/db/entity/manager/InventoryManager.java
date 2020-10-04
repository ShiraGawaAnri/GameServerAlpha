package com.nekonade.dao.db.entity.manager;


import com.nekonade.common.error.GameErrorException;
import com.nekonade.dao.db.entity.Inventory;
import com.nekonade.dao.db.entity.Prop;
import com.nekonade.dao.db.entity.Weapon;

public class InventoryManager {

    private final Inventory inventory;
    
    public InventoryManager(Inventory inventory) {
        this.inventory = inventory;
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
    public Prop getProp (String propId) {
        return inventory.getPropMap().get(propId);
    }
    public void checkItemEnough(String propId,int needCount) {
        Prop prop = this.getProp(propId);
        if(prop.getCount() < needCount) {
            throw GameErrorException.newBuilder(GameErrorCode.EquipWeaponCostNotEnough).message("需要{} {} ",prop,needCount).build();
        }
    }
    
    public int consumeProp(String id,int count) {
        Prop prop = this.getProp(id);
        int value = prop.getCount() - count;
        prop.setCount(value);
        return value;
    }
}
