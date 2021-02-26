package com.nekonade.neko.service;


import com.nekonade.dao.daos.GlobalConfigDao;
import com.nekonade.dao.db.entity.Character;
import com.nekonade.dao.db.entity.config.GlobalConfig;
import com.nekonade.dao.db.entity.data.CharactersDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ExampleCalcService {

    @Autowired
    private GlobalConfigDao globalConfigDao;

    public void CalcCharacterStatus(CharactersDB db, Character entity){
        String charaId = db.getCharaId();
        Map<String, GlobalConfig.Character.StatusDataBase> statusDataBase = globalConfigDao.getGlobalConfig().getCharacter().getStatusDataBase();
        GlobalConfig.Character.StatusDataBase dataBase = statusDataBase.get(charaId);
        if(dataBase == null){
            dataBase = new GlobalConfig.Character.StatusDataBase();
        }
    }
}
