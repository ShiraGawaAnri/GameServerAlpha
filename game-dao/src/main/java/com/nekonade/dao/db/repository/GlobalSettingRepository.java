package com.nekonade.dao.db.repository;

import com.nekonade.common.db.entity.setting.GlobalSetting;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GlobalSettingRepository extends MongoRepository<GlobalSetting, Long> {
}
