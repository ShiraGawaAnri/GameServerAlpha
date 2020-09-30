package com.nekonade.gamegateway.server.handler;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.nekonade.common.utils.JWTUtil;
import com.nekonade.common.utils.NettyUtils;
import com.nekonade.common.utils.TopicUtil;
import com.nekonade.gamegateway.common.GatewayServerConfig;
import com.nekonade.gamegateway.server.handler.codec.GateRequestMessage;
import com.nekonade.mq.system.mq.GameMQTemplate;
import com.nekonade.network.message.game.GameMessageHeader;
import com.nekonade.network.message.game.GameMessageInnerCodec;
import com.nekonade.server.balance.PlayerServiceInstance;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class DispatchGameMessageHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(DispatchGameMessageHandler.class);

    private final PlayerServiceInstance playerServiceInstance;// 注入业务服务管理类，从这里获取负载均衡的服务器信息
    private final GatewayServerConfig gatewayServerConfig; // 注入游戏网关服务配置信息。
    private JWTUtil.TokenContent tokenBody;
    private final GameMQTemplate gameMQTemplate;
    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    public DispatchGameMessageHandler(ApplicationContext applicationContext) {
        this.playerServiceInstance = applicationContext.getBean(PlayerServiceInstance.class);
        this.gameMQTemplate = applicationContext.getBean(GameMQTemplate.class);
        this.gatewayServerConfig = applicationContext.getBean(GatewayServerConfig.class);
        this.nacosDiscoveryProperties = applicationContext.getBean(NacosDiscoveryProperties.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GateRequestMessage requestMessage = (GateRequestMessage) msg;
        if (tokenBody == null) {// 如果首次通信，获取验证信息
            ConfirmHandler confirmHandler = (ConfirmHandler) ctx.channel().pipeline().get("ConfirmHandler");
            tokenBody = confirmHandler.getTokenBody();
        }
        String clientIp = NettyUtils.getRemoteIP(ctx.channel());
        GameMessageHeader header = requestMessage.getHeader();
        header.getAttribute().setClientIp(clientIp);
        dispatchMessage(requestMessage);
    }

    public void dispatchMessage(GateRequestMessage requestMessage) throws Exception {
        long playerId = tokenBody.getPlayerId();
        GameMessageHeader header = requestMessage.getHeader();
        int serviceId = header.getServiceId();
        String namespace = nacosDiscoveryProperties.getNamespace();
        int toServerId = playerServiceInstance.selectServerId(playerId, serviceId,namespace);
        if(toServerId == 0) {
            logger.error("找不到playerId {},serviceId {} 对应的目标服务器",playerId,serviceId);
        }

        header.setToServerId(toServerId);
        header.setFromServerId(gatewayServerConfig.getServerId());
        header.setPlayerId(playerId);
        String topic = TopicUtil.generateTopic(gatewayServerConfig.getBusinessGameMessageTopic(), toServerId);// 动态创建与业务服务交互的消息总线Topic
        byte[] value = GameMessageInnerCodec.encodeMessage(header, requestMessage.getByteBuf());
        gameMQTemplate.syncSendOrderly(topic, value,playerId);
        logger.debug("发送到{}消息成功->{}",topic, header);
    }
}
