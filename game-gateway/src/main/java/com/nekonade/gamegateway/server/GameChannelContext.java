package com.nekonade.gamegateway.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;

public class GameChannelContext {
    private final Channel channel;
    private final long playerId;

    public GameChannelContext(long playerId, Channel channel) {
        super();
        this.channel = channel;

        this.playerId = playerId;
    }

    public Channel getChannel() {
        return channel;
    }

    public long getPlayerId() {
        return playerId;
    }

    public Future<?> writeAndFlush(Object msg) {
        return channel.writeAndFlush(msg);
    }

    public Future<?> writeAndFlush(Object msg, ChannelPromise promise) {
        return channel.writeAndFlush(msg, promise);
    }

    public void sendEvent(Object msg) {
        this.channel.pipeline().fireUserEventTriggered(msg);
    }
}
