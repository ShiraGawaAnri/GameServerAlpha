package com.nekonade.dao.daos;

import com.nekonade.dao.db.entity.setting.GlobalSetting;
import com.nekonade.dao.db.repository.GlobalSettingRepository;
import com.nekonade.dao.redis.EnumRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public class GlobalSettingDao extends AbstractDao<GlobalSetting, Long>{

    @Autowired
    private GlobalSettingRepository globalSettingRepository;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.SETTINGS_GLOBAL;
    }

    @Override
    protected MongoRepository<GlobalSetting, Long> getMongoRepository() {
        return globalSettingRepository;
    }

    @Override
    protected Class<GlobalSetting> getEntityClass() {
        return GlobalSetting.class;
    }
}
