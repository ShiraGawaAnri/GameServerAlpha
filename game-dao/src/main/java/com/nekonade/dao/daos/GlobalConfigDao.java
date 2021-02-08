package com.nekonade.dao.daos;

import com.alibaba.fastjson.JSON;
import com.nekonade.dao.db.entity.config.GlobalConfig;
import com.nekonade.dao.db.repository.GlobalConfigRepository;
import com.nekonade.dao.redis.EnumRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class GlobalConfigDao extends AbstractDao<GlobalConfig, Long> {

    private final String GlobalConfigKey = EnumRedisKey.CONFIGS_GLOBAL.getKey().intern();
    private volatile GlobalConfig setting;
    @Autowired
    private GlobalConfigRepository globalConfigRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    protected EnumRedisKey getRedisKey() {
        return EnumRedisKey.CONFIGS_GLOBAL;
    }

    @Override
    protected MongoRepository<GlobalConfig, Long> getMongoRepository() {
        return globalConfigRepository;
    }

    @Override
    protected Class<GlobalConfig> getEntityClass() {
        return GlobalConfig.class;
    }

    public GlobalConfig getGlobalConfig() {
        if (setting == null) {
            this.findGlobalConfig();
        }
        return setting;
    }

    private void findGlobalConfig() {
        synchronized (GlobalConfigKey) {
            if (setting != null) return;
            this.updateGlobalConfig();
        }
    }

    public void updateGlobalConfig() {
        GlobalConfig result;
        String settingJson = redisTemplate.opsForValue().get(GlobalConfigKey);
        if (!StringUtils.isEmpty(settingJson)) {
            result = JSON.parseObject(settingJson, GlobalConfig.class);
        } else {
            Query query = new Query();
            query.with(Sort.by(Sort.Direction.DESC, "_id")).limit(1);
            result = mongoTemplate.findOne(query, GlobalConfig.class);
            if (result == null) {
                result = new GlobalConfig();
            }
        }
        this.saveOrUpdate(result, null);
        setting = result;
    }
}
