package com.nekonade.common.db.entity;


import com.nekonade.dao.daos.GlobalSettingDao;
import com.nekonade.common.db.entity.setting.GlobalSetting;
import com.nekonade.dao.db.repository.GlobalSettingRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Getter
@Setter
@ToString
public class Stamina {

    private Integer value = 20;

    private Long nextRecoverTime = 0L;

    private Long nextRecoverTimestamp = 0L;

    private Integer cutTime = 0;

    private double cutPercent = 0;

    @Autowired
    private GlobalSettingDao globalSettingDao;

    public Stamina(GlobalSettingDao globalSettingDao) {
        GlobalSetting globalSetting = null;
        if(globalSettingDao != null){
            List<GlobalSetting> all = globalSettingDao.findAll();
            if(all.size() >= 1){
                globalSetting = all.get(0);
            }
        }
        if(globalSetting == null){
            globalSetting = new GlobalSetting();
        }
        this.value = globalSetting.getStamina().getDefaultStarterValue();
        this.nextRecoverTime = globalSetting.getStamina().getRecoverTime();
        this.nextRecoverTimestamp = globalSetting.getStamina().getRecoverTime() + System.currentTimeMillis();
    }
}
