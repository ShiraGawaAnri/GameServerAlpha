package com.nekonade.game.client.command;

import com.alibaba.fastjson.JSONObject;
import com.nekonade.common.error.IServerError;
import com.nekonade.common.utils.CommonField;
import com.nekonade.common.utils.GameHttpClient;
import com.nekonade.game.client.common.ClientPlayerInfo;
import com.nekonade.game.client.service.GameClientBoot;
import com.nekonade.game.client.service.GameClientConfig;
import com.nekonade.network.param.error.GameCenterError;
import com.nekonade.network.param.game.message.ConfirmMsgRequest;
import com.nekonade.network.param.game.message.im.IMSendIMMsgRequest;
import com.nekonade.network.param.game.message.im.SendIMMsgRequest;
import com.nekonade.network.param.game.message.neko.EnterGameMsgRequest;
import com.nekonade.network.param.http.MessageCode;
import com.nekonade.network.param.http.request.CreatePlayerParam;
import com.nekonade.network.param.http.request.SelectGameGatewayParam;
import com.nekonade.network.param.http.response.GameGatewayInfoMsg;
import com.nekonade.network.param.http.response.ResponseEntity;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

import java.util.Random;

/**
 * 聊天的客户端命令
 * 
 * @ClassName: IMClientCommand
 * @Description: TODO
 * @author: wgs
 * @date: 2019年7月22日 下午9:06:06
 */
@ShellComponent
public class IMClientCommand {
	private final Logger logger = LoggerFactory.getLogger(IMClientCommand.class);
    @Autowired
    private ClientPlayerInfo playerInfo;
    @Autowired
    private GameClientConfig gameClientConfig;
    @Autowired
    private GameClientBoot gameClientBoot;
    private Header header;
    private String nickName;
    private String zoneId = "10003";
    private String token;
    public static boolean enteredGame = false;


    @ShellMethod("登陆账号,如果账号不存在，会自动创建,格式：login [username] [password]") // 连接服务器命令，
    public void login(@ShellOption String username,@ShellOption String password) {
        playerInfo.setUserName(username);
        playerInfo.setPassword(password);
        //从配置中获取游戏用户中心的rl，拼接Http请求地址
        String webGatewayUrl = gameClientConfig.getGameCenterUrl() + CommonField.GAME_CENTER_PATH + MessageCode.USER_LOGIN;
        JSONObject params = new JSONObject();
        params.put("openId",1);
        params.put("loginType", 1);
        params.put("username", username);
        params.put("password", password);
        //构造请求参数，并发送Http请求登陆，如果username不存在，服务端会创建新的账号，如果已存在，返回已存在的userId
        String result = GameHttpClient.post(webGatewayUrl, params);
        if(StringUtils.isEmpty(result)) {
            logger.info("账号登录失败:{}", result);
            return;
        }
        JSONObject responseJson2 = JSONObject.parseObject(result);
        if(!Integer.valueOf(0).equals(responseJson2.getInteger("code"))){
            logger.info("账号登录失败:{}",result);
            return;
        }
        JSONObject  responseJson = JSONObject.parseObject(result);
        //从返回消息中获取userId和token，记录下来，为以后的命令使用
        long userId = responseJson.getJSONObject("data").getLongValue("userId");
        token = responseJson.getJSONObject("data").getString("token");
        playerInfo.setUserId(userId);
        playerInfo.setToken(token);
        //将token验证放在Http的Header里面，以后的命令地请求Http的时候，需要携带，做权限验证
        header = new BasicHeader("user-token",token);
        logger.info("账号登陆成功:{} 自动连接默认区服:ZoneId={}",result,zoneId);
        this.getZoneInfo();
        this.selectGateway();
    }

    private void getZoneInfo(){

    }

    @ShellMethod("创建角色信息：cp [昵称]")
    public void cp (@ShellOption String nickName){
        this.createPlayer(nickName,zoneId);
    }

    @ShellMethod("创建角色信息： create-player [昵称] [区服ID]")
    public void createPlayer(@ShellOption String nickName,@ShellOption String zoneId) {
        CreatePlayerParam param = new CreatePlayerParam();
        param.setNickName(nickName);
        param.setZoneId(zoneId);
        String webGatewayUrl = gameClientConfig.getGameCenterUrl() + CommonField.GAME_CENTER_PATH + MessageCode.CREATE_PLAYER;
        //请求创建角色信息
        String result = GameHttpClient.post(webGatewayUrl, param,header);
        logger.info("创建角色返回:{}",result);
        JSONObject responseJson = JSONObject.parseObject(result);
        long playerId = responseJson.getJSONObject("data").getLongValue("playerId");
        playerInfo.setPlayerId(playerId);
        this.nickName = nickName;
        logger.info("角色PlayerId：{}",playerId);
    }

    @ShellMethod("切换区服: [区服ID]")
    public void sz (@ShellOption String zoneId){
        this.zoneId = zoneId;
        this.selectGateway();
    }


    @ShellMethod("连接网关：select-gateway")
    public void selectGateway() {
        try {
            String webGatewayUrl = gameClientConfig.getGameCenterUrl() + CommonField.GAME_CENTER_PATH + MessageCode.SELECT_GAME_GATEWAY;
            SelectGameGatewayParam param = new SelectGameGatewayParam();
            param.setZoneId(zoneId);
            param.setOpenId(playerInfo.getUserName());//暂代
            param.setToken(token);
            //从用户服务中心选择一个网关，获取网关的连接信息
            String result = GameHttpClient.post(webGatewayUrl, param,header);
            JSONObject responseJson = JSONObject.parseObject(result);
            if(!Integer.valueOf(0).equals(responseJson.getInteger("code"))){
                logger.info("选择网关失败:{}",result);
                return;
            }
            GameGatewayInfoMsg gameGatewayInfoMsg = ResponseEntity.parseObject(result, GameGatewayInfoMsg.class).getData();
            playerInfo.setGameGatewayInfoMsg(gameGatewayInfoMsg);
            gameClientConfig.setRsaPrivateKey(gameGatewayInfoMsg.getRsaPrivateKey());
            gameClientConfig.setGatewayToken(gameGatewayInfoMsg.getToken());
            gameClientConfig.setDefaultGameGatewayHost(gameGatewayInfoMsg.getIp());
            gameClientConfig.setDefaultGameGatewayPort(gameGatewayInfoMsg.getPort());
            logger.info("开始连接网关-{}:{}",gameGatewayInfoMsg.getIp(),gameGatewayInfoMsg.getPort());
            gameClientBoot.launch();//启动客户端，连接网关
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            logger.info("开始发送验证信息....");
//            ConfirmMsgRequest request = new ConfirmMsgRequest();
//            request.getBodyObj().setToken(gameClientConfig.getGatewayToken());
//            //发送连接验证，保证连接的正确性
//            gameClientBoot.getChannel().writeAndFlush(request);
//        logger.info("开始发送验证信息....2");
//        GameMessageHeader header = new GameMessageHeader();
//        GateRequestMessage gateRequestMessage = new GateRequestMessage(header, Unpooled.wrappedBuffer(request.write()),"");
//        gameClientBoot.getChannel().writeAndFlush(gateRequestMessage);
        }catch (Exception e) {
            logger.error("选择网关失败",e);
        }
    }

    @ShellMethod("进入游戏: enter-game")
    public void enterGame(){
        EnterGameMsgRequest request = new EnterGameMsgRequest();
        gameClientBoot.getChannel().writeAndFlush(request);
    }

    @ShellMethod("发送单服世界聊天信息：send [chat msg]")
    public void send(@ShellOption String chatMsg) {
        if(!enteredGame){
            logger.warn("请先进行Enter Game操作");
            return;
        }
    	SendIMMsgRequest request = new SendIMMsgRequest();
    	request.getBodyObj().setChat(chatMsg);
    	//向my-game-xinyue服务器发送聊天信息
    	gameClientBoot.getChannel().writeAndFlush(request);

    }

    @ShellMethod("sc chatmsg")
    public void sc(@ShellOption String chatMsg) {
        if(!enteredGame){
            logger.warn("请先进行Enter Game操作");
            return;
        }
        IMSendIMMsgRequest request = new IMSendIMMsgRequest();
        request.getBodyObj().setChat(chatMsg);
        request.getBodyObj().setSender(nickName);
        gameClientBoot.getChannel().writeAndFlush(request);
    }

    @ShellMethod("gacha times")
    public void gacha(@ShellOption Integer times) {

    }
}