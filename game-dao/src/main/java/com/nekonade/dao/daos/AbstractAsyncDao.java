package com.nekonade.dao.daos;

import com.nekonade.common.concurrent.GameEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * 之后考虑使用Mongodb的异步驱动实现异步操作。
 * @ClassName: AbstractAsyncDao 
 * @Description: TODO
 * @author: wang guang shuai
 * @date: 2020年1月6日 下午5:57:12
 */
public abstract class AbstractAsyncDao<Entity,ID,Dao extends AbstractDao<Entity, ID>> {
    protected Logger logger = null;
    private final GameEventExecutorGroup executorGroup;
    protected Dao dao;
    public AbstractAsyncDao(GameEventExecutorGroup executorGroup,Dao dao) {
        this.executorGroup = executorGroup;
        this.dao = dao;
        logger = LoggerFactory.getLogger(this.getClass());
    }
    
    public Dao getSyncDao() {
        return dao;
    }
    public void execute(ID id, Promise<?> promise, Runnable task) {
        EventExecutor executor = this.executorGroup.select(id.hashCode());
        executor.execute(() -> {
            try {
                task.run();
            } catch (Throwable e) {// 统一进行异常捕获，防止由于数据库查询的异常导到线程卡死
                logger.error("数据库操作失败,playerId:{}", id, e);
                if (promise != null) {
                    promise.setFailure(e);
                }
            }
        });
    }
    public void execute(ID id,  Runnable task) {
        EventExecutor executor = this.executorGroup.select(id.hashCode());
        executor.execute(() -> {
            try {
                task.run();
            } catch (Throwable e) {// 统一进行异常捕获，防止由于数据库查询的异常导到线程卡死
                logger.error("数据库操作失败,playerId:{}", id, e);
            }
        });
    }
    /**
     * 
     * <p>Description: 异步从数据库查找</p>
     * @param id
     * @param promise
     * @return
     * @author wang guang shuai 
     * @date  2020年1月10日 上午11:02:02
     *
     */
    public Future<Optional<Entity>> findById(ID id, Promise<Optional<Entity>> promise) {
        this.execute(id, promise, () -> {
            Optional<Entity> playerOp = dao.findByIdFromCacheOrLoader(id);
            promise.setSuccess(playerOp);
        });
        return promise;
    }
    /**
     * 
     * <p>Description:异步更新数据到数据库 </p>
     * @param player
     * @param promise
     * @author wgs 
     * @date  2019年6月14日 上午10:47:12
     *
     */
    public Promise<Boolean> saveOrUpdateToDB(ID id,Entity entity,Promise<Boolean> promise) {
        this.execute(id, promise, ()->{
            dao.saveOrUpdateToDB(entity);
            promise.setSuccess(true);
        });
        return promise;
    }
    /**
     * 
     * <p>Description:异步更新数据到redis </p>
     * @param player
     * @param promise
     * @author wgs 
     * @date  2019年6月14日 上午10:51:31
     *
     */
    public Promise<Boolean> saveOrUpdateToRedis(ID id,Entity entity,Promise<Boolean> promise) {
        this.execute(id,promise,()->{
           dao.saveOrUpdateToRedis(entity, id);
           promise.setSuccess(true);
        });
        return promise;
    }
    
}
