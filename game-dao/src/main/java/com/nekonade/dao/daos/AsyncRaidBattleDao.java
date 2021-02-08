package com.nekonade.dao.daos;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.dao.db.entity.RaidBattle;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

import java.util.Optional;

public class AsyncRaidBattleDao extends AbstractAsyncDao {

    private final RaidBattleDao raidBattleDao;

    public AsyncRaidBattleDao(GameEventExecutorGroup executorGroup, RaidBattleDao raidBattleDao) {
        super(executorGroup);
        this.raidBattleDao = raidBattleDao;
    }

    public Future<Optional<RaidBattle>> findRaidBattle(String raidId, Promise<Optional<RaidBattle>> promise) {
        this.execute(raidId, promise, () -> {
            Optional<RaidBattle> arena = raidBattleDao.findById(raidId);
            promise.setSuccess(arena);
        });
        return promise;
    }

    public void updateToRedis(String raidId, RaidBattle raidBattle, Promise<Boolean> promise) {
        this.execute(raidId, promise, () -> {
            raidBattleDao.saveOrUpdateToRedis(raidBattle, raidId);
            promise.setSuccess(true);
        });
    }

    public void updateToDB(String raidId, RaidBattle raidBattle, Promise<Boolean> promise) {
        this.execute(raidId, promise, () -> {
            raidBattleDao.saveOrUpdateToRedis(raidBattle, raidId);
            promise.setSuccess(true);
        });
    }

}
