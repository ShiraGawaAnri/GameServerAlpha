package com.nekonade.game.clienttest;

import com.google.protobuf.InvalidProtocolBufferException;
import com.nekonade.common.dto.CharacterDTO;
import com.nekonade.common.dto.MailDTO;
import com.nekonade.common.dto.RaidBattleDTO;
import com.nekonade.common.dto.RaidBattleDamageDTO;
import com.nekonade.common.model.PageResult;
import com.nekonade.common.utils.GameBase64Utils;
import com.nekonade.common.utils.RSAUtils;
import com.nekonade.game.clienttest.common.ClientPlayerInfo;
import com.nekonade.game.clienttest.common.PlayerInfo;
import com.nekonade.game.clienttest.common.RaidBattleInfo;
import com.nekonade.game.clienttest.service.GameClientBoot;
import com.nekonade.game.clienttest.service.GameClientConfig;
import com.nekonade.game.clienttest.service.handler.GameClientChannelContext;
import com.nekonade.game.clienttest.service.handler.HeartbeatHandler;
import com.nekonade.game.clienttest.service.handler.codec.DecodeHandler;
import com.nekonade.game.clienttest.service.handler.codec.EncodeHandler;
import com.nekonade.game.clienttest.test.ThreadContainer;
import com.nekonade.network.param.game.message.DoConfirmMsgResponse;
import com.nekonade.network.param.game.message.battle.JoinRaidBattleMsgResponse;
import com.nekonade.network.param.game.message.battle.RaidBattleAttackMsgResponse;
import com.nekonade.network.param.game.message.battle.RaidBattleBoardCastMsgResponse;
import com.nekonade.network.param.game.message.neko.*;
import com.nekonade.network.param.game.message.neko.error.GameErrorMsgResponse;
import com.nekonade.network.param.game.message.neko.error.GameGatewayErrorMsgResponse;
import com.nekonade.network.param.game.message.neko.error.GameNotificationMsgResponse;
import com.nekonade.network.param.game.messagedispatcher.GameMessageHandler;
import com.nekonade.network.param.game.messagedispatcher.GameMessageMapping;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;

import java.util.List;
import java.util.Map;

@GameMessageHandler
public class TestHandler {

    @Autowired
    private GameClientBoot gameClientBoot;

    private static final Logger logger = LoggerFactory.getLogger(TestHandler.class);


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

    @GameMessageMapping(DoConfirmMsgResponse.class)
    public void confirmResponse(DoConfirmMsgResponse response, GameClientChannelContext ctx) {
        String encryptAesKey = response.getBodyObj().getSecretKey();
        long playerId = response.getHeader().getPlayerId();
        GameClientConfig gameClientConfig = gameClientBoot.clientConfig.get(playerId);
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
            //logger.debug("连接认证成功,channelId:{}", ctx.getChannel().id().asShortText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @GameMessageMapping(DoEnterGameMsgResponse.class)
    public void enterGameResponse(DoEnterGameMsgResponse response, GameClientChannelContext ctx) {
        DoEnterGameMsgResponse.ResponseBody bodyObj = response.getBodyObj();
        //logger.debug("进入游戏成功：{}", bodyObj.getNickname());
        long playerId = response.getHeader().getPlayerId();
        PlayerInfo playerInfo = StressTestingTestNG.playerInfoMap.get(playerId);
        playerInfo.setEntered(true);
    }


    @GameMessageMapping(GetPlayerSelfMsgResponse.class)
    public void getPlayerSelfMsgResponse(GetPlayerSelfMsgResponse response, GameClientChannelContext ctx) {
        GetPlayerSelfMsgResponse.ResponseBody bodyObj = response.getBodyObj();
        long playerId = response.getHeader().getPlayerId();
        PlayerInfo playerInfo = StressTestingTestNG.playerInfoMap.get(playerId);

        BeanUtils.copyProperties(bodyObj,playerInfo);
        //logger.info("查询自身信息{}", response.bodyToString());
    }

    @GameMessageMapping(GetInventoryMsgResponse.class)
    public void getInventoryMsgResponse(GetInventoryMsgResponse response, GameClientChannelContext ctx) {
        //logger.info("查询仓库{}", response.bodyToString());
    }

    @GameMessageMapping(GetStaminaMsgResponse.class)
    public void getStaminaMsgResponse(GetStaminaMsgResponse response, GameClientChannelContext ctx) {
        //logger.info("查询疲劳{}", response.bodyToString());
    }

    @GameMessageMapping(GetArenaPlayerListMsgResponse.class)
    public void getArenaPlayerListMsgResponse(GetArenaPlayerListMsgResponse response, GameClientChannelContext ctx) {
        //logger.info("查询战场玩家列表{}", response.bodyToString());
    }

    @GameMessageMapping(TriggerPlayerLevelUpMsgResponse.class)
    public void levelUpdateMsgResponse(TriggerPlayerLevelUpMsgResponse response, GameClientChannelContext ctx) {
        //logger.info("玩家升级消息{}", response.bodyToString());
    }

    @GameMessageMapping(GetMailBoxMsgResponse.class)
    public void getMailBoxMsgResponse(GetMailBoxMsgResponse response, GameClientChannelContext ctx) {
        GetMailBoxMsgResponse.PageResult responseBodyObj = response.getBodyObj();
        PageResult<MailDTO> mail = new PageResult<>();
        BeanUtils.copyProperties(responseBodyObj, mail);
        //logger.info("玩家邮件信息{}", mail);
    }

    @GameMessageMapping(DoCreateBattleMsgResponse.class)
    public void createBattleMsgResponse(DoCreateBattleMsgResponse response, GameClientChannelContext ctx) {
        DoCreateBattleMsgResponse.RaidBattle bodyObj = response.getBodyObj();
        long playerId = response.getHeader().getPlayerId();
        RaidBattleInfo raidBattleInfo = StressTestingTestNG.raidBattleInfoMap.get(playerId);
        BeanUtils.copyProperties(bodyObj,raidBattleInfo);
        //logger.info("战斗信息返回 \r\nRaidId {} \r\n{}",raidBattleInfo.getRaidId(), response.bodyToString());
    }

    @GameMessageMapping(JoinRaidBattleMsgResponse.class)
    public void joinRaidBattleMsgResponse(JoinRaidBattleMsgResponse response,GameClientChannelContext ctx){
        RaidBattleDTO raidBattleDTO = new RaidBattleDTO();
        BeanUtils.copyProperties(response.getBodyObj(), raidBattleDTO);
        long playerId = response.getHeader().getPlayerId();
        RaidBattleInfo raidBattleInfo = StressTestingTestNG.raidBattleInfoMap.get(playerId);
        raidBattleInfo.setRaidId(raidBattleDTO.getRaidId());
        //logger.info("加入战斗结果返回 \r\nRaidId {} \r\n{}", raidBattleDTO.getRaidId(), response.bodyToString());
    }

    @GameMessageMapping(RaidBattleAttackMsgResponse.class)
    public void raidBattleAttackMsgResponse(RaidBattleAttackMsgResponse response, GameClientChannelContext ctx) throws InvalidProtocolBufferException {
        RaidBattleDamageDTO raidBattleDamageDTO = new RaidBattleDamageDTO();
        BeanUtils.copyProperties(response.getBodyObj(), raidBattleDamageDTO);
        //logger.info("战斗攻击信息返回 \r\nRaidId {} \r\n{}", raidBattleDamageDTO.getRaidId(), response.getBodyObj().toString());
        /*
        //Protobuf version
        RaidBattleAttackMsgBody.RaidBattleAttackMsgResponseBody responseBody = response.getResponseBody();
        JsonFormat.TypeRegistry typeRegistry = JsonFormat.TypeRegistry.newBuilder()
                .add(RaidBattleAttackMsgBody.Contribution.getDescriptor())
                .add(RaidBattleAttackMsgBody.Attack.getDescriptor())
                .build();
        String print = JsonFormat.printer().usingTypeRegistry(typeRegistry).print(responseBody);
        RaidBattleDamageDTO raidBattleDamageDTO = JacksonUtils.parseObjectV2(print, RaidBattleDamageDTO.class);
        logger.info("战斗攻击信息返回 \r\nRaidId {} \r\n{}", responseBody.getRaidId(), raidBattleDamageDTO.toString());*/
    }

    @GameMessageMapping(GetPlayerCharacterListMsgResponse.class)
    public void getPlayerCharacterListMsgResponse(GetPlayerCharacterListMsgResponse response, GameClientChannelContext ctx) {
        long playerId = response.getHeader().getPlayerId();
        PlayerInfo playerInfo = StressTestingTestNG.playerInfoMap.get(playerId);
        Map<String, CharacterDTO> characterMap = response.getBodyObj().getCharacterMap();
        characterMap.forEach((key, dto) -> {
            playerInfo.getCharacters().put(key,dto);
        });
        //logger.info("拥有角色列表{}", characterMap);
    }

    @GameMessageMapping(DoDiamondGachaMsgResponse.class)
    public void diamondGachaMsgResponse(DoDiamondGachaMsgResponse response, GameClientChannelContext ctx) {
        long playerId = response.getHeader().getPlayerId();
        PlayerInfo playerInfo = StressTestingTestNG.playerInfoMap.get(playerId);
        List<CharacterDTO> list = response.getBodyObj().getResult();
        list.forEach((dto) -> {
            playerInfo.getCharacters().put(dto.getCharaId(),dto);
        });
        //logger.info("抽奖列表{}", list);
    }

    @GameMessageMapping(RaidBattleBoardCastMsgResponse.class)
    public void raidBattleBoardCastMsgResponse(RaidBattleBoardCastMsgResponse response, GameClientChannelContext ctx){

    }

}
