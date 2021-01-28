package com.nekonade.game.client.service;

import com.nekonade.game.client.command.IMClientCommand;
import com.nekonade.game.client.service.handler.DispatchGameMessageHandler;
import com.nekonade.game.client.service.handler.HeartbeatHandler;
import com.nekonade.game.client.service.handler.codec.DecodeHandler;
import com.nekonade.game.client.service.handler.codec.EncodeHandler;
import com.nekonade.game.client.service.handler.codec.ResponseHandler;
import com.nekonade.network.param.game.GameMessageService;
import com.nekonade.network.param.game.message.ConfirmMsgRequest;
import com.nekonade.network.param.game.messagedispatcher.DispatchGameMessageService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameClientBoot {

    @Autowired
    private GameClientConfig gameClientConfig;
    @Autowired
    private GameMessageService gameMessageService;
    @Autowired
    private DispatchGameMessageService dispatchGameMessageService;
    @Autowired
    private GameClientBoot gameClientBoot;

    private Bootstrap bootStrap;
    private EventLoopGroup eventGroup;
    private final Logger logger = LoggerFactory.getLogger(GameClientBoot.class);
    private Channel channel;

    public void launch() {
        if (channel != null) {
            channel.close();
        }
        eventGroup = new NioEventLoopGroup(gameClientConfig.getWorkThreads());// 从配置中获取处理业务的线程数
        bootStrap = new Bootstrap();
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
                ch.pipeline().addLast("DecodeHandler", new DecodeHandler());// 添加解码
                ch.pipeline().addLast("responseHandler", new ResponseHandler(gameMessageService));//将响应消息转化为对应的响应对象
                // ch.pipeline().addLast(new TestGameMessageHandler());//测试handler
                ch.pipeline().addLast(new IdleStateHandler(150, 6, 200));//如果6秒之内没有消息写出，发送写出空闲事件，触发心跳
                ch.pipeline().addLast("HeartbeatHandler", new HeartbeatHandler());//心跳Handler
                ch.pipeline().addLast(new DispatchGameMessageHandler(dispatchGameMessageService));// 添加逻辑处理

            }
        });
        ChannelFuture future = bootStrap.connect(gameClientConfig.getDefaultGameGatewayHost(), gameClientConfig.getDefaultGameGatewayPort());
        channel = future.channel();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    logger.debug("连接{}:{}成功,channelId:{}", gameClientConfig.getDefaultGameGatewayHost(),
                            gameClientConfig.getDefaultGameGatewayPort(), future.channel().id().asShortText());
                    logger.info("开始发送验证信息....");
                    ConfirmMsgRequest request = new ConfirmMsgRequest();
                    request.getBodyObj().setToken(gameClientConfig.getGatewayToken());
                    //发送连接验证，保证连接的正确性
                    gameClientBoot.getChannel().writeAndFlush(request);
                } else {
                    Throwable e = future.cause();
                    logger.error("连接失败-{}", e);
                }
            }
        });
    }

    public Channel getChannel() {
        return channel;
    }

}
