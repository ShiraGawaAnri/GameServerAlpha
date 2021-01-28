package com.nekonade.common.db.entity.manager;


import com.nekonade.common.db.entity.Hero;
import com.nekonade.common.db.entity.Player;
import com.nekonade.common.db.entity.Weapon;

public class PlayerManager {
    private final Player player;//声明数据对象
    private final HeroManager heroManager; //英雄管理类
    private final TaskManager taskManager;
    private final InventoryManager inventoryManager;
    //声明其它的管理类....
    public PlayerManager(Player player) {//初始化所的管理类
        this.player = player;
        this.heroManager = new HeroManager(player);
        this.taskManager = new TaskManager(player.getTask());
        this.inventoryManager = new InventoryManager(player.getInventory());
        //其它的管理类.....
    }
    public Player getPlayer() {
        return player;
    }
    public int addPlayerExp(int exp) {
        //添加角色经验，判断是否升级，返回升级后当前最新的等级
        return player.getLevel();
    }
    
    public Weapon getWeapon(String weaponId) {
        return this.inventoryManager.getWeapon(weaponId);
    }
    public Hero getHero(String heroId) {
        return this.heroManager.getHero(heroId);
    }
    public HeroManager getHeroManager() {
        return heroManager;
    }
    public TaskManager getTaskManager() {
        return taskManager;
    }
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
    
    
}
