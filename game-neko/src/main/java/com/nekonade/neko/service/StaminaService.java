package com.nekonade.neko.service;

import com.nekonade.common.error.GameErrorException;
import com.nekonade.dao.db.entity.Stamina;
import com.nekonade.dao.db.entity.manager.GameErrorCode;
import com.nekonade.dao.db.entity.manager.PlayerManager;
import com.nekonade.dao.db.entity.manager.StaminaManager;
import com.nekonade.dao.db.entity.setting.GlobalSetting;
import com.nekonade.dao.db.repository.GlobalSettingRepository;
import com.nekonade.neko.common.DataConfigService;
import com.nekonade.neko.logic.functionevent.EquipWeaponEvent;
import com.nekonade.neko.logic.functionevent.StaminaRecoverEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaminaService {

    @Autowired
    private DataConfigService dataConfigService;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private GlobalSettingRepository globalSettingRepository;

    private void recoverStaminaByAuto(Stamina stamina){
        Integer value = stamina.getValue();
        List<GlobalSetting> all = globalSettingRepository.findAll();
        GlobalSetting globalSetting = null;
        if(all.size() > 0){
            globalSetting = all.get(0);
        }
        if(globalSetting == null){
            globalSetting = new GlobalSetting();
        }
        GlobalSetting.Stamina settingStamina = globalSetting.getStamina();

        long now = System.currentTimeMillis();
        int maxValue = 999;
        long cTime = settingStamina.getRecoverTime();
        int recoverValue = settingStamina.getRecoverValue();
        if(now > stamina.getPreQueryTime()){
            long v = (now - stamina.getPreQueryTime()) / cTime * recoverValue;
            if(v > 0){
                value += (int) v;
                value = Math.min(value,maxValue);
                stamina.setPreQueryTime(now);
                stamina.setValue(value);
            }
            stamina.setNextRecoverTime(stamina.getPreQueryTime() + cTime - now);
            stamina.setNextRecoverTimestamp(stamina.getPreQueryTime() + cTime);
        }
    }

    private void addOnePointStamina(Stamina stamina){
        stamina.setValue(stamina.getValue() + 1);
    }

    public void getStamina(PlayerManager playerManager){
        //Test 每次访问都加1
        StaminaManager staminaManager = playerManager.getStaminaManager();
        Stamina stamina = staminaManager.getStamina();
        if (stamina == null) {
            throw GameErrorException.newBuilder(GameErrorCode.Stamina).build();
        }
        this.recoverStaminaByAuto(stamina);
        this.addOnePointStamina(stamina);
        playerManager.getPlayer().setStamina(stamina);
        staminaManager.setStamina(stamina);
        StaminaRecoverEvent event = new StaminaRecoverEvent(this,playerManager);
        context.publishEvent(event);
    }
}
