package com.nekonade.dao.daos;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.RaidBattle;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import org.springframework.beans.BeanUtils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AsyncRaidBattleDao extends AbstractAsyncDao {

    private final RaidBattleDao raidBattleDao;

    public AsyncRaidBattleDao(GameEventExecutorGroup executorGroup, RaidBattleDao raidBattleDao) {
        super(executorGroup);
        this.raidBattleDao = raidBattleDao;
    }

    public CompletableFuture<Optional<RaidBattle>> findRaidBattle(String raidId) {
        AsyncRaidBattleDao that = this;
        return CompletableFuture.supplyAsync(()-> that.raidBattleDao.findByRaidId(raidId));
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

    public String findPlayerFromRedis(String raidId) {
        return this.raidBattleDao.findRaidBattleFromRedis(raidId);
    }

    public CompletableFuture<Boolean> saveOrUpdateRaidBattleToRedis(RaidBattle raidBattle) {
        AsyncRaidBattleDao that = this;
        return CompletableFuture.supplyAsync(()->{
            that.raidBattleDao.saveOrUpdateToRedis(raidBattle, raidBattle.getRaidId());
            return true;
        },that.getEventExecutor(raidBattle.getRaidId()));
    }

    public CompletableFuture<Boolean> saveOrUpdateRaidBattleToDB(RaidBattle raidBattle) {
        AsyncRaidBattleDao that = this;
        return CompletableFuture.supplyAsync(()->{
            that.raidBattleDao.saveOrUpdateToDB(raidBattle);
            return true;
        },that.getEventExecutor(raidBattle.getRaidId()));
    }

    public void syncFlushRaidBattle(RaidBattle raidBattle) {
        this.raidBattleDao.saveOrUpdate(raidBattle, raidBattle.getRaidId());
    }
}
