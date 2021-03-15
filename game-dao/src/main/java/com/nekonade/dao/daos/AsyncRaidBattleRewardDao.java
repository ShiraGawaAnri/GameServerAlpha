package com.nekonade.dao.daos;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import com.nekonade.dao.db.entity.RaidBattleReward;
import io.netty.util.concurrent.Promise;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class AsyncRaidBattleRewardDao extends AbstractAsyncDao {

    private final RaidBattleRewardDao dao;

    public AsyncRaidBattleRewardDao(GameEventExecutorGroup executorGroup, RaidBattleRewardDao dao) {
        super(executorGroup);
        this.dao = dao;
    }

    public CompletableFuture<Optional<RaidBattleReward>> findUnclaimed(long playerId, String raidId) {
        AsyncRaidBattleRewardDao that = this;
        RaidBattleReward entity = new RaidBattleReward();
        entity.setClaimed(0);
        entity.setPlayerId(playerId);
        entity.setRaidId(raidId);
        return CompletableFuture.supplyAsync(() -> that.dao.findByEntity(entity));
    }

    public void updateToRedis(String raidId, RaidBattleReward entity, Promise<Boolean> promise) {
        this.execute(raidId, promise, () -> {
            dao.saveOrUpdateToRedis(entity, raidId);
            promise.setSuccess(true);
        });
    }

    public void updateToDB(RaidBattleReward entity, Promise<Boolean> promise) {
        this.execute(entity.getPlayerId(), promise, () -> {
            dao.saveOrUpdateToDB(entity);
            promise.setSuccess(true);
        });
    }

    public void updateToDB(RaidBattleReward entity) {
        this.execute(entity.getPlayerId(), null, () -> {
            dao.saveOrUpdateToDB(entity);
        });
    }


    public CompletableFuture<Boolean> saveOrUpdateRaidBattleRewardToDB(RaidBattleReward entity) {
        AsyncRaidBattleRewardDao that = this;
        return CompletableFuture.supplyAsync(() -> {
            that.dao.saveOrUpdateToDB(entity);
            return true;
        }, that.getEventExecutor(entity.getRaidId()));
    }
}
