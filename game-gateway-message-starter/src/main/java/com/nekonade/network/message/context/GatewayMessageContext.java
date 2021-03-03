package com.nekonade.network.message.context;

import com.nekonade.common.gameMessage.DataManager;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.network.message.channel.AbstractGameChannelHandlerContext;
import com.nekonade.network.message.manager.PlayerManager;
import com.nekonade.common.gameMessage.GameMessageHeader;
import com.nekonade.common.gameMessage.IGameMessage;
import com.nekonade.network.param.game.messagedispatcher.IGameChannelContext;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class GatewayMessageContext<T extends DataManager> implements IGameChannelContext {
    private final IGameMessage requestMessage;
    private final AbstractGameChannelHandlerContext ctx;
    //    @Deprecated
//    private final Player player;// 这里的Player只是为了兼容前面的测试代码，在实例开发中，可以去掉这个参数
//    @Deprecated
//    private final PlayerManager playerManager;// 这里是为了兼容前面的测试代码，在实际开发中，可以去掉
    private final T dataManager;


    public GatewayMessageContext(T dataManager, IGameMessage requestMessage, AbstractGameChannelHandlerContext ctx) {
        this.requestMessage = requestMessage;
        this.ctx = ctx;
        this.dataManager = dataManager;
    }

//    @Deprecated
//    // 这里面的Player和PlayerManager参数是为了兼容前面的测试代码，在实际应用中可以去掉
//    public GatewayMessageContext(T dataManager, Player player, PlayerManager playerManager, IGameMessage requestMessage, AbstractGameChannelHandlerContext ctx) {
//        this.requestMessage = requestMessage;
//        this.ctx = ctx;
//        this.playerManager = playerManager;
//        this.player = player;
//        this.dataManager = dataManager;
//    }

    public T getDataManager() {
        return dataManager;
    }

    @Override
    public void sendMessage(IGameMessage response) {
        if (response != null) {
            wrapResponseMessage(response);
            ctx.writeAndFlush(response);
        }
    }

    private void wrapResponseMessage(IGameMessage response) {
        GameMessageHeader responseHeader = response.getHeader();
        GameMessageHeader requestHeader = this.requestMessage.getHeader();
        responseHeader.setClientSendTime(requestHeader.getClientSendTime());
        responseHeader.setClientSeqId(requestHeader.getClientSeqId());
        responseHeader.setPlayerId(requestHeader.getPlayerId());
        responseHeader.setServerSendTime(System.currentTimeMillis());
        responseHeader.setToServerId(requestHeader.getFromServerId());
        responseHeader.setFromServerId(requestHeader.getToServerId());
        responseHeader.setVersion(requestHeader.getVersion());
    }

    /**
     * 将同一条消息广播给本服的所有人
     * <p>Description: </p>
     *
     * @param message
     * @author wgs
     * @date 2019年7月25日 下午9:27:28
     */
    public void broadcastMessage(IGameMessage message) {
        if (message != null) {
            ctx.gameChannel().getEventDispatchService().broadcastMessage(message);
        }
    }

    public void broadcastMessage(IGameMessage message, long... playerIds) {
        ctx.gameChannel().getEventDispatchService().broadcastMessage(message, playerIds);
    }

    /**
     * <p>
     * Description:如果发送的请求，需要处理返回值，就使用这个方法发送rpc请求
     * </p>
     *
     * @param rpcRequest
     * @param callback
     * @return
     * @author wgs
     * @date 2019年6月17日 下午5:16:09
     */
    public Future<IGameMessage> sendRPCMessage(IGameMessage rpcRequest, Promise<IGameMessage> callback) {
        if (rpcRequest != null) {
            rpcRequest.getHeader().setPlayerId(ctx.gameChannel().getPlayerId());
            ctx.writeRPCMessage(rpcRequest, callback);
        } else {
            throw new NullPointerException("RPC消息不能为空");
        }
        return callback;
    }

    /**
     * <p>
     * Description:如果发送的rpc请求不需要处理返回结果，就使用这个方法
     * </p>
     *
     * @param rpcRequest
     * @author wgs
     * @date 2019年6月17日 下午5:16:31
     */
    public void sendRPCMessage(IGameMessage rpcRequest) {
        if (rpcRequest != null) {
            ctx.writeRPCMessage(rpcRequest, null);
        } else {
            throw new NullPointerException("RPC消息不能为空");
        }
    }

    /**
     * <p>
     * Description:向某个playerId的GameChannel中发送一个事件
     * </p>
     *
     * @param event
     * @param promise
     * @param playerId
     * @author wgs
     * @date 2019年6月9日 下午5:05:50
     */
    public Future<Object> sendUserEvent(Object event, Promise<Object> promise, long playerId) {
        ctx.gameChannel().getEventDispatchService().fireUserEvent(playerId, event, promise);
        return promise;
    }


    public <E> DefaultPromise<E> newPromise() {
        return new DefaultPromise<>(ctx.executor());
    }

    public DefaultPromise<IGameMessage> newRPCPromise() {
        return new DefaultPromise<>(ctx.executor());
    }

    public Player getPlayer() {
        return this.getPlayerManager().getPlayer();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> E getRequest() {
        return (E) this.requestMessage;
    }

    @Override
    public String getRemoteHost() {
        return this.requestMessage.getHeader().getAttribute().getClientIp();
    }


    @Override
    public long getPlayerId() {
        return this.requestMessage.getHeader().getPlayerId();
    }

    public PlayerManager getPlayerManager() {
        return (PlayerManager) dataManager;
    }


}
