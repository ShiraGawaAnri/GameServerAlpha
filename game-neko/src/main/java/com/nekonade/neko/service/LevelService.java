package com.nekonade.neko.service;

import com.nekonade.dao.daos.GlobalConfigDao;
import com.nekonade.neko.common.DataConfigService;
import com.nekonade.network.message.event.function.StaminaRecoverEvent;
import com.nekonade.network.message.manager.PlayerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class LevelService {

    @Autowired
    private DataConfigService dataConfigService;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private GlobalConfigDao globalConfigDao;

    public void checkLevelUp(PlayerManager playerManager) {
        StaminaRecoverEvent event = new StaminaRecoverEvent(this, playerManager);
        context.publishEvent(event);
    }
}
