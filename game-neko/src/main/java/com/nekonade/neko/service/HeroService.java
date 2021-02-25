package com.nekonade.neko.service;

import com.nekonade.dao.db.entity.Character;
import com.nekonade.dao.db.entity.Weapon;
import com.nekonade.neko.common.DataConfigService;
import com.nekonade.neko.dataconfig.EquipWeaponDataConfig;
import com.nekonade.network.message.manager.PlayerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class HeroService {

    @Autowired
    private DataConfigService dataConfigService;
    @Autowired
    private ApplicationContext context;

    public void addHeroWeapon(PlayerManager playerManager, String heroId, String weaponId) {
        /*HeroManager heroManager = playerManager.getHeroManager();
        Character character = heroManager.getHero(heroId);
        if (character == null) {
            throw GameNotifyException.newBuilder(GameErrorCode.HeroNotExist).build();
        }
        if (character.getWeaponId() != null) {
            throw GameNotifyException.newBuilder(GameErrorCode.HeroHadEquipedWeapon).build();
        }
        InventoryManager inventoryManager = playerManager.getInventoryManager();
        Weapon weapon = inventoryManager.getWeapon(weaponId);
        if (weapon == null) {
            throw GameNotifyException.newBuilder(GameErrorCode.WeaponNotExist).build();
        }
        if (!weapon.isEnable()) {
            throw GameNotifyException.newBuilder(GameErrorCode.WeaponUnenable).build();
        }
        EquipWeaponDataConfig equipWeaponDataConfig = this.dataConfigService.getDataConfig(weaponId, EquipWeaponDataConfig.class);
        if (character.getLevel() < equipWeaponDataConfig.getLevel()) {
            throw GameNotifyException.newBuilder(GameErrorCode.HeroLevelNotEnough).message("需要等级：{}", 20).build();
        }
        Item item = inventoryManager.getItem(equipWeaponDataConfig.getCostId());
        if (item.getAmount() < equipWeaponDataConfig.getCostCount()) {
            throw GameNotifyException.newBuilder(GameErrorCode.EquipWeaponCostNotEnough).message("需要{} {} ", equipWeaponDataConfig.getCostId(), equipWeaponDataConfig.getCostCount()).build();
        }
        inventoryManager.consumeItem(equipWeaponDataConfig.getCostId(), equipWeaponDataConfig.getCostCount());
        character.setWeaponId(weaponId);
        weapon.setEnable(false);
        EquipWeaponEvent event = new EquipWeaponEvent(this);
        context.publishEvent(event);*/
    }

    public void addHeroWeaponNew(String heroId, String weaponId, PlayerManager playerManager) {
        /*this.checkAddHeroWeaponParam(heroId, weaponId, playerManager);//检测参数
        Character character = playerManager.getHero(heroId);
        Weapon weapon = playerManager.getWeapon(weaponId);
        EquipWeaponDataConfig equipWeaponDataConfig = this.dataConfigService.getDataConfig(weaponId, EquipWeaponDataConfig.class);
        this.checkAddHeroWeaponCondition(character, weapon, playerManager, equipWeaponDataConfig);//检测条件
        this.actionEquipWeapon(character, weapon, playerManager, equipWeaponDataConfig);//执行业务*/
    }

    private void checkAddHeroWeaponParam(String heroId, String weaponId, PlayerManager playerManager) {
        /*HeroManager heroManager = playerManager.getHeroManager();
        InventoryManager inventoryManager = playerManager.getInventoryManager();
        heroManager.checkHeroExist(heroId);// 检测英雄是否存在
        inventoryManager.checkWeaponExist(weaponId);//检测是否拥有这个武器*/
    }

    private void checkAddHeroWeaponCondition(Character character, Weapon weapon, PlayerManager playerManager, EquipWeaponDataConfig equipWeaponDataConfig) {
        /*HeroManager heroManager = playerManager.getHeroManager();
        InventoryManager inventoryManager = playerManager.getInventoryManager();
        heroManager.checkHadEquipWeapon(character);// 检测英雄是否已装备武器
        inventoryManager.checkWeaponHadEquipped(weapon);// 检测这个武器是否已装备到其它英雄身上
        heroManager.checkHeroLevelEnough(character.getLevel(), equipWeaponDataConfig.getLevel());// 检测英雄等级是否足够
        inventoryManager.checkItemEnough(equipWeaponDataConfig.getCostId(), equipWeaponDataConfig.getCostCount());//检测消耗的道具是否跢*/
    }

    private void actionEquipWeapon(Character character, Weapon weapon, PlayerManager playerManager, EquipWeaponDataConfig equipWeaponDataConfig) {
        /*InventoryManager inventoryManager = playerManager.getInventoryManager();
        inventoryManager.consumeItem(equipWeaponDataConfig.getCostId(), equipWeaponDataConfig.getCostCount());
        character.setWeaponId(weapon.getWeaponId());
        weapon.setEnable(false);
        EquipWeaponEvent event = new EquipWeaponEvent(this);
        context.publishEvent(event);*/
    }


}
