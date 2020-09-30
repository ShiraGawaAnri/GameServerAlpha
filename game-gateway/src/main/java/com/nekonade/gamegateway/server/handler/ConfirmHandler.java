package com.nekonade.gamegateway.server.handler;

import com.nekonade.common.utils.JWTUtil;
import com.nekonade.common.utils.NettyUtils;
import com.nekonade.gamegateway.common.GatewayServerConfig;
import com.nekonade.gamegateway.server.ChannelService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

public class ConfirmHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmHandler.class);

    private final GatewayServerConfig serverConfig;// 注入服务端配置
    private final boolean confirmSuccess = false;// 标记连接是否认证成功
    private ScheduledFuture<?> future;// 定时器的返回值
    @Getter
    private JWTUtil.TokenContent tokenBody;

    private final ChannelService channelService;

    public ConfirmHandler(ApplicationContext applicationContext) {
        this.serverConfig = applicationContext.getBean(GatewayServerConfig.class);
        this.channelService = applicationContext.getBean(ChannelService.class);
    }

    // 此方法会在连接建立成功channel注册之后调用
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("客户端 {} 连接成功，channelId:{}", NettyUtils.getRemoteIP(ctx.channel()),
                ctx.channel().id().asShortText());
        int delay = serverConfig.getWaitConfirmTimeoutSecond();// 从配置中获取延迟时间
        future = ctx.channel().eventLoop().schedule(() -> {
            if (!confirmSuccess && ctx.channel().isActive()) {// 如果没有认证成功，则关闭连接。
                logger.debug("连接认证超时，断开连接，channelId:{}", ctx.channel().id().asShortText());
                ctx.close();
            }
        }, delay, TimeUnit.SECONDS);
        ctx.fireChannelActive();
    }
}
