package com.nekonade.network.message.manager;


import com.nekonade.dao.db.entity.Hero;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.Weapon;
import com.nekonade.network.message.channel.GameChannel;
import lombok.Getter;
import org.springframework.context.ApplicationContext;

public class PlayerManager {

    @Getter
    private final ApplicationContext context;

    @Getter
    private final GameChannel gameChannel;

    private final Player player;//声明数据对象
    private final HeroManager heroManager; //英雄管理类
    private final TaskManager taskManager;
    private final InventoryManager inventoryManager;
    private final StaminaManager staminaManager;
    private final ExperienceManager experienceManager;

    //声明其它的管理类....
    public PlayerManager(Player player, ApplicationContext applicationContext, GameChannel gameChannel) {//初始化所的管理类
        this.context = applicationContext;
        this.gameChannel = gameChannel;
        this.player = player;
        this.heroManager = new HeroManager(this);
        this.taskManager = new TaskManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.staminaManager = new StaminaManager(this);
        this.experienceManager = new ExperienceManager(this);
    }

    //    public int addPlayerExp(int exp) {
//        //添加角色经验，判断是否升级，返回升级后当前最新的等级
//        return player.getLevel();
//    }
    public Player getPlayer() {
        return player;
    }

    public ExperienceManager getExperienceManager() {
        return experienceManager;
    }

    public StaminaManager getStaminaManager() {
        return staminaManager;
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


    public Weapon getWeapon(String weaponId) {
        return this.inventoryManager.getWeapon(weaponId);
    }

    public Hero getHero(String heroId) {
        return this.heroManager.getHero(heroId);
    }


}
