package com.nekonade.dao.daos;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.dao.db.entity.Arena;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

import java.util.Optional;

public class AsyncArenaDao extends AbstractAsyncDao {
    private final ArenaDao arenaDao;


    public AsyncArenaDao(GameEventExecutorGroup executorGroup, ArenaDao arenaDao) {
        super(executorGroup);
        this.arenaDao = arenaDao;
    }

    public Future<Optional<Arena>> findArena(Long playerId, Promise<Optional<Arena>> promise) {
        this.execute(playerId, promise, () -> {
            Optional<Arena> arena = arenaDao.findById(playerId);
            promise.setSuccess(arena);
        });
        return promise;
    }

    public void updateToRedis(long playerId, Arena arena, Promise<Boolean> promise) {
        this.execute(playerId, promise, () -> {
            arenaDao.saveOrUpdateToRedis(arena, playerId);
            promise.setSuccess(true);
        });
    }

    public void updateToDB(long playerId, Arena arena, Promise<Boolean> promise) {
        this.execute(playerId, promise, () -> {
            arenaDao.saveOrUpdateToRedis(arena, playerId);
            promise.setSuccess(true);
        });
    }

}
