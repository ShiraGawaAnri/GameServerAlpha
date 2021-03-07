package com.nekonade.jmetertest.service.logichandler;

import com.nekonade.common.utils.GameBase64Utils;
import com.nekonade.common.utils.GameTimeUtils;
import com.nekonade.common.utils.RSAUtils;
import com.nekonade.jmetertest.StressTesting;
import com.nekonade.network.param.game.message.DoConfirmMsgResponse;
import com.nekonade.network.param.game.message.HeartbeatMsgResponse;
import com.nekonade.network.param.game.message.neko.error.GameErrorMsgResponse;
import com.nekonade.network.param.game.message.neko.error.GameGatewayErrorMsgResponse;
import com.nekonade.network.param.game.message.neko.error.GameNotificationMsgResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import com.nekonade.jmetertest.common.PlayerInfo;
import com.nekonade.jmetertest.service.GameClientConfig;
import com.nekonade.jmetertest.service.handler.GameClientChannelContext;
import com.nekonade.jmetertest.service.handler.HeartbeatHandler;
import com.nekonade.jmetertest.service.handler.codec.DecodeHandler;
import com.nekonade.jmetertest.service.handler.codec.EncodeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

@GameMessageHandler
public class SystemMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(SystemMessageHandler.class);

    static {
        gameClientConfig = StressTesting.gameClientConfig;
        playerInfo = StressTesting.playerInfo;
    }

    public static GameClientConfig gameClientConfig;

    public static PlayerInfo playerInfo;

    @GameMessageMapping(DoConfirmMsgResponse.class)
    public void confirmResponse(DoConfirmMsgResponse response, GameClientChannelContext ctx) {
        String encryptAesKey = response.getBodyObj().getSecretKey();
        byte[] content = Base64Utils.decodeFromString(encryptAesKey);
        try {
            byte[] privateKey = GameBase64Utils.decodeFromString(gameClientConfig.getRsaPrivateKey());
            byte[] valueBytes = RSAUtils.decryptByPrivateKey(content, privateKey);
            String value = new String(valueBytes);// 得到明文的aes加密密钥
            DecodeHandler decodeHandler = (DecodeHandler) ctx.getChannel().pipeline().get("DecodeHandler");
            decodeHandler.setAesSecretKey(value);// 把密钥给解码Handler
            EncodeHandler encodeHandler = (EncodeHandler) ctx.getChannel().pipeline().get("EncodeHandler");
            encodeHandler.setAesSecretKey(value);// 把密钥给编码Handler
            HeartbeatHandler heartbeatHandler = (HeartbeatHandler) ctx.getChannel().pipeline().get("HeartbeatHandler");
            heartbeatHandler.setConfirmSuccess(true);
            logger.info("连接认证成功,channelId:{}", ctx.getChannel().id().asShortText());
            playerInfo.setConnected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GameMessageMapping(HeartbeatMsgResponse.class)
    public void heartbeatResponse(HeartbeatMsgResponse response, GameClientChannelContext ctx) {
        logger.trace("服务器心跳返回，当前服务器时间：{}", GameTimeUtils.getStringDate(response.getBodyObj().getServerTime()));
    }

    @GameMessageMapping(GameGatewayErrorMsgResponse.class)
    public void gameGatewayErrorMsgResponse(GameGatewayErrorMsgResponse response, GameClientChannelContext ctx) {
        logger.warn("网关返回报错{}", response.bodyToString());
    }

    @GameMessageMapping(GameErrorMsgResponse.class)
    public void gameErrorMsgResponse(GameErrorMsgResponse response, GameClientChannelContext ctx) {
        logger.warn("服务器返回报错{}", response.bodyToString());
    }

    @GameMessageMapping(GameNotificationMsgResponse.class)
    public void gameNotificationMsgResponse(GameNotificationMsgResponse response, GameClientChannelContext ctx) {
        logger.warn("弹框提醒{}", response.bodyToString());
    }
}
