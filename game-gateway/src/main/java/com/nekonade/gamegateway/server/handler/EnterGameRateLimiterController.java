package com.nekonade.gamegateway.server.handler;


import com.google.common.util.concurrent.RateLimiter;
import com.nekonade.gamegateway.common.WaitLinesConfig;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class EnterGameRateLimiterController {

    private final RateLimiter rateLimiter;

    //同一秒允许多少人登录
    private final double maxPermits;

    //等待获取登录许可的请求个数，原则上可以通过maxPermits推算
    private final double maxWaitingRequests;

    //当前队列
    @Getter
    private final ConcurrentHashMap<Long, Long> waitLoginDeque;

    private final EventExecutor eventExecutor = new DefaultEventExecutor();

    //定时器
    private final ScheduledFuture<?> clearIdlePlayerId;

    public int getLineLength(){
        return waitLoginDeque.size();
    }

    public double getRestTime(){
        return getLineLength() / (rateLimiter.getRate() != 0 ? rateLimiter.getRate() : 1);
    }

    //只允许以warm up的形式处理
    public EnterGameRateLimiterController(WaitLinesConfig waitLinesConfig) {
        this.maxPermits = waitLinesConfig.getMaxPermits();
        this.maxWaitingRequests = waitLinesConfig.getMaxWaitingRequests();
        this.rateLimiter = RateLimiter.create(maxPermits);
        this.waitLoginDeque = new ConcurrentHashMap<>();
        long checkDelaySeconds = waitLinesConfig.getCheckDelaySeconds();
        long fakeSeconds = waitLinesConfig.getFakeSeconds();
        clearIdlePlayerId = eventExecutor.scheduleWithFixedDelay(()->{
            long now = System.currentTimeMillis();
            waitLoginDeque.forEach(1,(key,value)->{
                if(now - value > fakeSeconds * 1000){
                    waitLoginDeque.remove(key);
                }
            });
        }, checkDelaySeconds, checkDelaySeconds, TimeUnit.SECONDS);

    }

    public boolean acquire(long playerId) {
        boolean success = rateLimiter.tryAcquire(1);
        //当有排队人数时
        if (success) {
            /*if (waitLoginDeque.size() > 0) {
                if (waitLoginDeque.get(playerId) == null) {
                    //如果不在排队，则让他排队
                    return false;
                }
            }*/
            rateLimiter.acquire();//可能有出入
            waitLoginDeque.remove(playerId);
            return true;
        }
        if (waitLoginDeque.size() > maxWaitingRequests) {
            return false;
        }
        waitLoginDeque.put(playerId, System.currentTimeMillis());
        return false;
    }
}
