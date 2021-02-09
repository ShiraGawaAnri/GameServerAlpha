package com.nekonade.neko.service;

import com.nekonade.dao.daos.GlobalConfigDao;
import com.nekonade.dao.db.entity.Experience;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.config.GlobalConfig;
import com.nekonade.neko.common.DataConfigService;
import com.nekonade.network.message.event.basic.LevelUpEvent;
import com.nekonade.network.message.event.function.ExperienceEvent;
import com.nekonade.network.message.manager.PlayerManager;
import io.netty.util.concurrent.DefaultPromise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ExperienceService {

    @Autowired
    private DataConfigService dataConfigService;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private GlobalConfigDao globalConfigDao;

    @EventListener
    public void checkExperience(ExperienceEvent event) {
        PlayerManager playerManager = event.getPlayerManager();
        Player player = playerManager.getPlayer();
        int playerLevel = player.getLevel();
        Experience experience = player.getExperience();
        GlobalConfig globalConfig = globalConfigDao.getGlobalConfig();
        GlobalConfig.Level level = globalConfig.getLevel();
        long nextLevelUpPoint = level.getNextLevelUpPoint(playerLevel);
        int playerLevelUpCount = 0;
        while (experience.getExp() >= nextLevelUpPoint && playerLevel < level.getMaxValue()) {
            playerLevel++;
            playerLevelUpCount++;
            player.setLevel(Math.min(playerLevel, level.getMaxValue()));
            nextLevelUpPoint = level.getNextLevelUpPoint(playerLevel);
        }
        if (playerLevel >= level.getMaxValue()) {
            long maxLevelExperience = level.getNextLevelUpPoint(Math.max(level.getMaxValue() - 1, 1));
            experience.setExp(maxLevelExperience);
            experience.setNextLevelExp(maxLevelExperience);
        }
        if (playerLevelUpCount > 0) {
            int addStamina = playerLevelUpCount * globalConfig.getStamina().getEachLevelAddPoint();
            playerManager.getStaminaManager().addStamina(addStamina);
            LevelUpEvent levelUpEvent = new LevelUpEvent();
            levelUpEvent.setAfterLevel(playerLevel);
            levelUpEvent.setBeforeLevel(playerLevel - playerLevelUpCount);
            levelUpEvent.setNowStamina(playerManager.getStaminaManager().getStamina().getValue());
            levelUpEvent.setBeforeStamina(playerManager.getStaminaManager().getStamina().getValue() - addStamina);
            levelUpEvent.setNextLevelExperience(experience.getNextLevelExp());
            DefaultPromise<Object> promise = new DefaultPromise<>(playerManager.getGameChannel().getChannelPiple().gameChannel().executor());
//            promise.addListener(future->{
//                if((Boolean) future.get()){
//                    System.out.println("升级消息发送成功");
//                }
//            });
            playerManager.getGameChannel().getEventDispatchService().fireUserEvent(player.getPlayerId(), levelUpEvent, promise);
        }
    }
}
