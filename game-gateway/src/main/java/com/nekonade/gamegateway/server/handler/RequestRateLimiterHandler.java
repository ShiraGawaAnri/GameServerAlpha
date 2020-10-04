package com.nekonade.gamegateway.server.handler;

import com.google.common.util.concurrent.RateLimiter;
import com.nekonade.network.param.game.common.GameMessagePackage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestRateLimiterHandler extends ChannelInboundHandlerAdapter {
    private final RateLimiter globalRateLimiter; // 全局限制器
    private static RateLimiter userRateLimiter;// 用户限流器，用于限制单个用户的请求。
    private static final Logger logger = LoggerFactory.getLogger(RequestRateLimiterHandler.class);
    private int lastClientSeqId = 0;

    public RequestRateLimiterHandler(RateLimiter globalRateLimiter, double requestPerSecond) {
        this.globalRateLimiter = globalRateLimiter;
        userRateLimiter = RateLimiter.create(requestPerSecond);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!userRateLimiter.tryAcquire()) {// 获取令牌失败，触发限流
            logger.debug("channel {} 请求过多，连接断开", ctx.channel().id().asShortText());
            ctx.close();
            return;
        }
        if (!globalRateLimiter.tryAcquire()) {// 获取全局令牌失败，触发限流
            logger.debug("全局请求超载，channel {} 断开", ctx.channel().id().asShortText());
            ctx.close();
            return;
        }

        GameMessagePackage gameMessagePackage = (GameMessagePackage)msg;
        int clientSeqId = gameMessagePackage.getHeader().getClientSeqId();
        if (lastClientSeqId > 0) {
            if (clientSeqId <= lastClientSeqId) {
                return;//直接返回，不再处理。
            }
        }
        this.lastClientSeqId = clientSeqId;
        ctx.fireChannelRead(msg);//不要忘记添加这个，要不然后面的handler收不到消息
    }
}
