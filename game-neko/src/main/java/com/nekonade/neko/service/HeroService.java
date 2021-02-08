package com.nekonade.neko.service;

import com.nekonade.common.error.GameNotification;
import com.nekonade.dao.db.entity.Hero;
import com.nekonade.dao.db.entity.Item;
import com.nekonade.dao.db.entity.Weapon;
import com.nekonade.neko.common.DataConfigService;
import com.nekonade.neko.dataconfig.EquipWeaponDataConfig;
import com.nekonade.network.message.event.function.EquipWeaponEvent;
import com.nekonade.network.message.manager.GameErrorCode;
import com.nekonade.network.message.manager.HeroManager;
import com.nekonade.network.message.manager.InventoryManager;
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
        HeroManager heroManager = playerManager.getHeroManager();
        Hero hero = heroManager.getHero(heroId);
        if (hero == null) {
            throw GameNotification.newBuilder(GameErrorCode.HeroNotExist).build();
        }
        if (hero.getWeaponId() != null) {
            throw GameNotification.newBuilder(GameErrorCode.HeroHadEquipedWeapon).build();
        }
        InventoryManager inventoryManager = playerManager.getInventoryManager();
        Weapon weapon = inventoryManager.getWeapon(weaponId);
        if (weapon == null) {
            throw GameNotification.newBuilder(GameErrorCode.WeaponNotExist).build();
        }
        if (!weapon.isEnable()) {
            throw GameNotification.newBuilder(GameErrorCode.WeaponUnenable).build();
        }
        EquipWeaponDataConfig equipWeaponDataConfig = this.dataConfigService.getDataConfig(weaponId, EquipWeaponDataConfig.class);
        if (hero.getLevel() < equipWeaponDataConfig.getLevel()) {
            throw GameNotification.newBuilder(GameErrorCode.HeroLevelNotEnough).message("需要等级：{}", 20).build();
        }
        Item item = inventoryManager.getItem(equipWeaponDataConfig.getCostId());
        if (item.getCount() < equipWeaponDataConfig.getCostCount()) {
            throw GameNotification.newBuilder(GameErrorCode.EquipWeaponCostNotEnough).message("需要{} {} ", equipWeaponDataConfig.getCostId(), equipWeaponDataConfig.getCostCount()).build();
        }
        inventoryManager.consumeItem(equipWeaponDataConfig.getCostId(), equipWeaponDataConfig.getCostCount());
        hero.setWeaponId(weaponId);
        weapon.setEnable(false);
        EquipWeaponEvent event = new EquipWeaponEvent(this);
        context.publishEvent(event);
    }

    public void addHeroWeaponNew(String heroId, String weaponId, PlayerManager playerManager) {
        this.checkAddHeroWeaponParam(heroId, weaponId, playerManager);//检测参数
        Hero hero = playerManager.getHero(heroId);
        Weapon weapon = playerManager.getWeapon(weaponId);
        EquipWeaponDataConfig equipWeaponDataConfig = this.dataConfigService.getDataConfig(weaponId, EquipWeaponDataConfig.class);
        this.checkAddHeroWeaponCondition(hero, weapon, playerManager, equipWeaponDataConfig);//检测条件
        this.actionEquipWeapon(hero, weapon, playerManager, equipWeaponDataConfig);//执行业务
    }

    private void checkAddHeroWeaponParam(String heroId, String weaponId, PlayerManager playerManager) {
        HeroManager heroManager = playerManager.getHeroManager();
        InventoryManager inventoryManager = playerManager.getInventoryManager();
        heroManager.checkHeroExist(heroId);// 检测英雄是否存在
        inventoryManager.checkWeaponExist(weaponId);//检测是否拥有这个武器
    }

    private void checkAddHeroWeaponCondition(Hero hero, Weapon weapon, PlayerManager playerManager, EquipWeaponDataConfig equipWeaponDataConfig) {
        HeroManager heroManager = playerManager.getHeroManager();
        InventoryManager inventoryManager = playerManager.getInventoryManager();
        heroManager.checkHadEquipWeapon(hero);// 检测英雄是否已装备武器
        inventoryManager.checkWeaponHadEquiped(weapon);// 检测这个武器是否已装备到其它英雄身上
        heroManager.checkHeroLevelEnough(hero.getLevel(), equipWeaponDataConfig.getLevel());// 检测英雄等级是否足够
        inventoryManager.checkItemEnough(equipWeaponDataConfig.getCostId(), equipWeaponDataConfig.getCostCount());//检测消耗的道具是否跢
    }

    private void actionEquipWeapon(Hero hero, Weapon weapon, PlayerManager playerManager, EquipWeaponDataConfig equipWeaponDataConfig) {
        InventoryManager inventoryManager = playerManager.getInventoryManager();
        inventoryManager.consumeItem(equipWeaponDataConfig.getCostId(), equipWeaponDataConfig.getCostCount());
        hero.setWeaponId(weapon.getWeaponId());
        weapon.setEnable(false);
        EquipWeaponEvent event = new EquipWeaponEvent(this);
        context.publishEvent(event);
    }


}
