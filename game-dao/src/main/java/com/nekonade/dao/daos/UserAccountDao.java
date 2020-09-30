package com.nekonade.dao.daos;

import com.nekonade.dao.db.entity.UserAccount;
import com.nekonade.dao.db.repository.UserAccountRepository;
import com.nekonade.dao.redis.DaoRedisKeyConifg;
import com.nekonade.dao.redis.IRedisKeyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAccountDao extends AbstractDao<UserAccount, Long> {

    @Autowired
    private UserAccountRepository repository;

    @Override
    protected IRedisKeyConfig getRedisKey() {
        return DaoRedisKeyConifg.USER_ACCOUNT;
    }

    @Override
    protected MongoRepository<UserAccount, Long> getMongoRepository() {
        return repository;
    }

    @Override
    protected Class<UserAccount> getEntityClass() {
        return UserAccount.class;
    }

    public Optional<UserAccount> getUserAccountByUserName(String userName) {
        long userId = this.getUserIdByUserName(userName);
        if(userId > 0) {
            return this.findByIdFromCacheOrLoader(userId);
        }
        return Optional.empty();
    }
    /**
     * 设置用户名和id的映射
     * @param userAccount
     */
    public void setUserNameIDMapper(UserAccount userAccount) {
    	 IRedisKeyConfig enumRedisKey = DaoRedisKeyConifg.USER_NAME_MAPPER_ID;
         String key = enumRedisKey.getKey(userAccount.getUserName());
         redisTemplate.opsForValue().set(key, String.valueOf(userAccount.getUserId()));
    }
    
    private long getUserIdByUserName(String userName) {
        IRedisKeyConfig enumRedisKey = DaoRedisKeyConifg.USER_NAME_MAPPER_ID;
        String key = enumRedisKey.getKey(userName);
        String userId = redisCacheTemplate.getValue(key, userName, enumRedisKey.getExpire(), p->{
          UserAccount result = this.repository.findByUserName(userName);
          if(result != null) {
              this.saveOrUpdateToRedis(result, result.getUserId());
          }
          return result == null ? null : String.valueOf(result.getUserId());
        });
        return userId == null ? 0 :Long.parseLong(userId);
    }
    
    
    
    
}
