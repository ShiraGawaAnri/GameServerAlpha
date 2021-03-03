package com.nekonade.dao.daos;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.common.constants.RedisConstants;
import com.nekonade.common.redis.EnumRedisKey;
import com.nekonade.dao.db.entity.LogGameLogic;
import com.nekonade.dao.db.entity.RaidBattle;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AsyncLogDao extends AbstractAsyncDao {

    private final LogGameLogicDao gameLogicDao;

    private final StringRedisTemplate redisTemplate;

    public AsyncLogDao(GameEventExecutorGroup executorGroup, LogGameLogicDao gameLogicDao, StringRedisTemplate redisTemplate) {
        super(executorGroup);
        this.gameLogicDao = gameLogicDao;
        this.redisTemplate = redisTemplate;
    }

    public void saveGameLogicLog(LogGameLogic entity) {
        String operatorId = entity.getOperatorId();
        this.execute(operatorId, null, () -> {
            gameLogicDao.saveLog(entity);
        });
    }
}
