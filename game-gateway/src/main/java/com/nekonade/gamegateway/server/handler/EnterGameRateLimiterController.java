package com.nekonade.gamegateway.server.handler;


import com.google.common.util.concurrent.RateLimiter;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
public class EnterGameRateLimiterController {

    private final RateLimiter rateLimiter;

    //同一秒允许多少人登录
    private double maxPermits;

    //等待获取登录许可的请求个数，原则上可以通过maxPermits推算
    private double maxWaitingRequests;

    //当前队列
    private final ConcurrentHashMap<Long, Long> waitLoginDeque;

    //只允许以warm up的形式处理
    public EnterGameRateLimiterController(double maxPermits, long warmUpPeriodAsSecond, double maxWaitingRequests) {
        this.maxPermits = maxPermits;
        this.maxWaitingRequests = maxWaitingRequests;
        this.rateLimiter = RateLimiter.create(maxPermits, warmUpPeriodAsSecond, TimeUnit.SECONDS);
        this.waitLoginDeque = new ConcurrentHashMap<>();
        //TODO:创建定时器,定时移除多余的排队人员
    }

    public boolean acquire(long playerId) {
        boolean success = rateLimiter.tryAcquire();
        //当有排队人数时
        if (success) {
            if (waitLoginDeque.size() > 0) {
                if (waitLoginDeque.get(playerId) == null) {
                    return false;
                }
            }
            rateLimiter.acquire();//可能有出入
            waitLoginDeque.remove(playerId);
            return true;
        }
        if (waitLoginDeque.size() > maxWaitingRequests) {
            return false;
        }
        if (waitLoginDeque.get(playerId) == null) {
            waitLoginDeque.put(playerId, System.currentTimeMillis());
        }
        System.out.println("排队登录人数 : " + waitLoginDeque.size());
        return false;
    }
}
