package com.nekonade.neko.service;

import com.nekonade.dao.daos.GlobalConfigDao;
import com.nekonade.dao.db.entity.Experience;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.config.GlobalConfig;
import com.nekonade.neko.common.DataConfigService;
import com.nekonade.network.message.event.function.ExperienceEvent;
import com.nekonade.network.message.event.user.TriggerPlayerLevelUpEventUser;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.network.param.game.message.neko.TriggerPlayerLevelUpMsgResponse;
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
            TriggerPlayerLevelUpEventUser triggerPlayerLevelUpEventUser = new TriggerPlayerLevelUpEventUser();
            triggerPlayerLevelUpEventUser.setAfterLevel(playerLevel);
            triggerPlayerLevelUpEventUser.setBeforeLevel(playerLevel - playerLevelUpCount);
            triggerPlayerLevelUpEventUser.setNowStamina(playerManager.getStaminaManager().getStamina().getValue());
            triggerPlayerLevelUpEventUser.setBeforeStamina(playerManager.getStaminaManager().getStamina().getValue() - addStamina);
            triggerPlayerLevelUpEventUser.setNextLevelExperience(experience.getNextLevelExp());
            DefaultPromise<Object> promise = new DefaultPromise<>(playerManager.getGameChannel().getChannelPiple().gameChannel().executor());
            promise.addListener(future->{
                if(future.isSuccess()){
                    TriggerPlayerLevelUpMsgResponse response = (TriggerPlayerLevelUpMsgResponse)future.get();
                    playerManager.getGameChannel().pushMessage(response);
                }
            });
            playerManager.getGameChannel().getEventDispatchService().fireUserEvent(player.getPlayerId(), triggerPlayerLevelUpEventUser, promise);
        }
    }
}
