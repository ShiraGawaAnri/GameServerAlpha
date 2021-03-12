package com.nekonade.gamegateway.server.handler;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.nekonade.gamegateway.config.WaitLinesConfig;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class EnterGameRateLimiterController {

    private final RateLimiter rateLimiter;

    //同一秒允许多少人登录
    private final double loginPermitsPerSeconds;

    //等待获取登录许可的请求个数，原则上可以通过maxPermits推算
    private final long maxWaitingRequests;

    private final long warmUpPeriodSeconds;

    private final long tryAccquireWait;

    //当前队列
    /*@Getter
    private final ConcurrentHashMap<Long, Long> waitLoginDeque;*/

    private final EventExecutor eventExecutor = new DefaultEventExecutor();


    @Getter
    private final LoadingCache<Long, Long> waitLoginDeque;

    public long getLineLength(){
        return waitLoginDeque.size();
    }

    public double getRestTime(){
        return getLineLength() / (rateLimiter.getRate() != 0 ? rateLimiter.getRate() : 1);
    }

    //只允许以warm up的形式处理
    public EnterGameRateLimiterController(WaitLinesConfig waitLinesConfig) {
        this.loginPermitsPerSeconds = waitLinesConfig.getLoginPermitsPerSeconds();
        this.warmUpPeriodSeconds = waitLinesConfig.getWarmUpPeriodSeconds();
        this.maxWaitingRequests = waitLinesConfig.getMaxWaitingRequests();
        this.rateLimiter = RateLimiter.create(loginPermitsPerSeconds);
        long fakeSeconds = waitLinesConfig.getFakeSeconds();
        waitLoginDeque = CacheBuilder.newBuilder().maximumSize(this.maxWaitingRequests).expireAfterAccess(fakeSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    public Long load(Long key) throws Exception {
                        return System.currentTimeMillis();
                    }
                });
        this.tryAccquireWait = (long)(1 / loginPermitsPerSeconds * 1000);
    }

    @SneakyThrows
    public Double acquire(long playerId) {
        boolean success = rateLimiter.tryAcquire(1,tryAccquireWait,TimeUnit.MILLISECONDS);
        //当有排队人数时
        if (success) {
            /*if (waitLoginDeque.size() > 0) {
                if (waitLoginDeque.get(playerId) == null) {
                    //如果不在排队，则让他排队
                    return false;
                }
            }*/
            waitLoginDeque.invalidate(playerId);
        }else{
            if (waitLoginDeque.size() > maxWaitingRequests) {
                return null;
            }
            waitLoginDeque.get(playerId);
        }
        return rateLimiter.acquire(1);//可能有出入
    }
}
