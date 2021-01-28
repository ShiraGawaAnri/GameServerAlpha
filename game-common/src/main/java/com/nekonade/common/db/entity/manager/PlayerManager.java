package com.nekonade.common.db.entity.manager;


import com.nekonade.common.db.entity.Hero;
import com.nekonade.common.db.entity.Player;
import com.nekonade.common.db.entity.Weapon;
import lombok.Getter;

public class PlayerManager {

    private final Player player;//声明数据对象
    private final HeroManager heroManager; //英雄管理类
    private final TaskManager taskManager;
    private final InventoryManager inventoryManager;
    private final StaminaManager staminaManager;
    //声明其它的管理类....
    public PlayerManager(Player player) {//初始化所的管理类
        this.player = player;
        this.heroManager = new HeroManager(player);
        this.taskManager = new TaskManager(player.getTask());
        this.inventoryManager = new InventoryManager(player.getInventory());
        this.staminaManager = new StaminaManager(player.getStamina());
        //其它的管理类.....
    }

    public int addPlayerExp(int exp) {
        //添加角色经验，判断是否升级，返回升级后当前最新的等级
        return player.getLevel();
    }
    public Player getPlayer() {
        return player;
    }
    public TaskManager getTaskManager() {
        return taskManager;
    }
    public HeroManager getHeroManager() {
        return heroManager;
    }
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public StaminaManager getStaminaManager() {
        return staminaManager;
    }

    public Weapon getWeapon(String weaponId) {
        return this.inventoryManager.getWeapon(weaponId);
    }
    public Hero getHero(String heroId) {
        return this.heroManager.getHero(heroId);
    }
    
    
}
