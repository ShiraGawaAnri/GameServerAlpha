package com.nekonade.dao.db.entity.manager;


import com.nekonade.dao.db.entity.Stamina;
import com.nekonade.dao.db.entity.setting.GlobalSetting;
import com.nekonade.dao.db.repository.GlobalSettingRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class StaminaManager {


    @Getter
    @Setter
    private Stamina stamina;

    public StaminaManager(Stamina stamina) {
        this.stamina = stamina;
    }

}
