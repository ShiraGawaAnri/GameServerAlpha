package com.nekonade.dao.daos;


import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.dao.db.entity.UserAccount;
import com.nekonade.dao.redis.DaoRedisKeyConifg;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class AsyncUserAccountDao extends AbstractAsyncDao<UserAccount, Long, AbstractDao<UserAccount, Long>> {

    public AsyncUserAccountDao(GameEventExecutorGroup executorGroup, AbstractDao<UserAccount, Long> dao) {
        super(executorGroup, dao);
    }

    /**
     * 
     * <p>Description:更新用户的活跃时间 </p>
     * @param userAccount
     * @author wang guang shuai 
     * @date  2020年1月10日 下午3:59:16
     *
     */
    public void updateUserAccountExpire(UserAccount userAccount) {
        this.execute(userAccount.getUserId(), () -> {
            // 更新名字和id的映射过期时间
            this.updateExpire(DaoRedisKeyConifg.USER_NAME_MAPPER_ID, userAccount.getUserName());
            // 更新用户信息过期时间
            this.updateExpire(DaoRedisKeyConifg.USER_ACCOUNT, userAccount.getUserId());
        });

    }
    private void updateExpire(DaoRedisKeyConifg redisKey,Object param) {
        String userNameIDKey = redisKey.getKey(param.toString());
        Duration expire = redisKey.getExpire();
        if (expire != null) {
            this.getSyncDao().redisTemplate.expire(userNameIDKey, expire.toMillis(), TimeUnit.MINUTES);
        }
    }

}
