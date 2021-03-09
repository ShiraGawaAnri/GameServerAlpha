package com.nekonade.game.clienttest.service;

import com.nekonade.game.clienttest.service.handler.DispatchGameMessageHandler;
import com.nekonade.game.clienttest.service.handler.HeartbeatHandler;
import com.nekonade.game.clienttest.service.handler.codec.DecodeHandler;
import com.nekonade.game.clienttest.service.handler.codec.EncodeHandler;
import com.nekonade.game.clienttest.service.handler.codec.ResponseHandler;
import com.nekonade.network.param.game.GameMessageService;
import com.nekonade.network.param.game.message.DoConfirmMsgRequest;
import com.nekonade.network.param.game.messagedispatcher.DispatchGameMessageService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
public class GameClientBoot {

    private static final Logger logger = LoggerFactory.getLogger(GameClientBoot.class);
    @Autowired
    private ApplicationContext context;
    @Autowired
    private GameMessageService gameMessageService;
    @Autowired
    private DispatchGameMessageService dispatchGameMessageService;

    public static final Map<Long,Channel> playerChannel = new ConcurrentHashMap<>();

    public static final Map<Long,GameClientConfig> clientConfig = new ConcurrentHashMap<>();

    public static final Map<Long,Bootstrap> bootstrapMap = new ConcurrentHashMap<>();

    public static final Map<Long,EventLoopGroup> eventLoopGroupMap = new ConcurrentHashMap<>();

    @SneakyThrows
    public CompletableFuture<Boolean> launch(long playerId,GameClientConfig gameClientConfig) {
        Channel channel = playerChannel.get(playerId);
        if (channel != null) {
            channel.close();
        }
        clientConfig.put(playerId,gameClientConfig);
        EventLoopGroup eventGroup = eventLoopGroupMap.get(playerId);
        if (eventGroup == null) {
            eventGroup = new NioEventLoopGroup(gameClientConfig.getWorkThreads());
            eventLoopGroupMap.put(playerId,eventGroup);
        }
        // 从配置中获取处理业务的线程数
        Bootstrap bootStrap = bootstrapMap.get(playerId);
        if (bootStrap == null) {
            bootStrap = new Bootstrap();
            bootstrapMap.put(playerId,bootStrap);
        }
        bootStrap
                .group(eventGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, gameClientConfig.getConnectTimeout() * 1000)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast("EncodeHandler", new EncodeHandler(gameClientConfig));// 添加编码
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 1024 * 8, 0, 4, -4, 0));
                        //ch.pipeline().addLast(new Decoder());//自定义拆包
                        ch.pipeline().addLast("DecodeHandler", new DecodeHandler(context));// 添加解码
                        ch.pipeline().addLast("responseHandler", new ResponseHandler(gameMessageService));//将响应消息转化为对应的响应对象
                        // ch.pipeline().addLast(new TestGameMessageHandler());//测试handler
                        ch.pipeline().addLast(new IdleStateHandler(150, 60, 200));//如果6秒之内没有消息写出，发送写出空闲事件，触发心跳
                        ch.pipeline().addLast("HeartbeatHandler", new HeartbeatHandler());//心跳Handler
                        ch.pipeline().addLast(new DispatchGameMessageHandler(dispatchGameMessageService));// 添加逻辑处理

                    }
                });
        ChannelFuture future = bootStrap.connect(gameClientConfig.getDefaultGameGatewayHost(), gameClientConfig.getDefaultGameGatewayPort());
        channel = future.channel();
        Channel finalChannel = channel;
        playerChannel.put(playerId,finalChannel);
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                logger.debug("连接{}:{}成功,channelId:{}", gameClientConfig.getDefaultGameGatewayHost(),
                        gameClientConfig.getDefaultGameGatewayPort(), future1.channel().id().asShortText());
                logger.info("开始发送验证信息....");
                DoConfirmMsgRequest request = new DoConfirmMsgRequest();
                request.getBodyObj().setToken(gameClientConfig.getGatewayToken());
                //发送连接验证，保证连接的正确性
                finalChannel.writeAndFlush(request);
            } else {
                Throwable e = future1.cause();
                logger.error("连接失败-{}", e);
            }
        });
        CompletableFuture<Boolean> booleanCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return true;
        });
        return booleanCompletableFuture;
    }

    public Channel getChannel(long playerId) {
        return playerChannel.get(playerId);
    }

}

