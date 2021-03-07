package com.nekonade.jmetertest;

import com.alibaba.fastjson.JSONObject;
import com.nekonade.common.utils.CommonField;
import com.nekonade.common.utils.GameHttpClient;
import com.nekonade.jmetertest.service.TestDispatchGameMessageService;
import com.nekonade.network.param.game.GameMessageService;
import com.nekonade.network.param.game.message.neko.DoEnterGameMsgRequest;
import com.nekonade.network.param.game.messagedispatcher.DispatchGameMessageService;
import com.nekonade.network.param.http.MessageCode;
import com.nekonade.network.param.http.request.CreatePlayerParam;
import com.nekonade.network.param.http.request.SelectGameGatewayParam;
import com.nekonade.network.param.http.response.GameGatewayInfoMsg;
import com.nekonade.network.param.http.response.ResponseEntity;
import com.nekonade.jmetertest.common.ClientPlayerInfo;
import com.nekonade.jmetertest.common.PlayerInfo;
import com.nekonade.jmetertest.common.RaidBattleInfo;
import com.nekonade.jmetertest.service.GameClientBoot;
import com.nekonade.jmetertest.service.GameClientConfig;
import io.netty.channel.ChannelFuture;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class StressTesting extends AbstractJavaSamplerClient implements Serializable {

    public StressTesting() {
        logger.info(whoAmI() + "\tConstruct" + " Run Nekonade Stress Test");
    }

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(StressTesting.class);

    private Header header;
    private String zoneId = "10003";
    private String token;
    private String username;
    private String password;
    private String nickname;

    public static ClientPlayerInfo loginPlayerInfo;

    public static PlayerInfo playerInfo;

    public static RaidBattleInfo raidBattleInfo;

    public static GameClientConfig gameClientConfig;

    public static GameClientBoot gameClientBoot;

    public static GameMessageService gameMessageService;

    public static TestDispatchGameMessageService dispatchGameMessageService;

    static {
        loginPlayerInfo = new ClientPlayerInfo();
        playerInfo = new PlayerInfo();
        raidBattleInfo = new RaidBattleInfo();
        gameClientConfig = new GameClientConfig();
        gameClientBoot = new GameClientBoot();
        gameMessageService = new GameMessageService();
        gameMessageService.init();
        dispatchGameMessageService = new TestDispatchGameMessageService(gameMessageService);
        dispatchGameMessageService.scanGameMessages(0, "com.nekonade");
    }


    /**
     * 执行runTest()方法前会调用此方法,可放一些初始化代码
     */
    @Override
    public void setupTest(JavaSamplerContext arg0) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(RandomUtils.nextInt()).append(System.currentTimeMillis());
        String oridin = UUID.randomUUID().toString() + buffer.toString();
        oridin = DigestUtils.md5Hex(oridin);
        StringBuffer stringBuffer = new StringBuffer();
        int index = RandomUtils.nextInt(4, 9);
        for(int i = 0; i< Math.min(oridin.length(),index); i++){
            stringBuffer.append(oridin.charAt(i));
        }
        username = stringBuffer.toString();
        password = stringBuffer.toString();
    }

    /**
     * JMeter测试用例入口
     */
    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        logger.info("Test start");
        SampleResult result = new SampleResult();
        result.setSampleLabel("NekoNade");
        result.sampleStart();
        try{
            Login(username,password);
            createPlayer();
            long playerId = loginPlayerInfo.getPlayerId();
            if(playerId == 0){
                throw new Exception("创建角色失败");
            }
            Boolean connect = connect();
            if(!connect){
                throw new Exception("连接失败");
            }
            enterGame();
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Future<Boolean> waitEnter = executorService.submit(() -> {
                while (!playerInfo.isEntered()){
                    Thread.sleep(1000);
                };
                return true;
            });
            Boolean entered = waitEnter.get(100, TimeUnit.SECONDS);
            if(!entered){
                throw new Exception("进入游戏超时");
            }
            result.setResponseData(playerInfo.toString(),"UTF-8");
            result.setSuccessful(true);
        }catch (Exception e){
            result.setSamplerData(e.getMessage());
            e.printStackTrace();
            result.setSuccessful(false);
        }
        result.sampleEnd();
        return result;
    }

    /**
     * JMeter界面中可手工输入参数,代码里面通过此方法获取
     */
    public Arguments getDefaultParameters() {

        Arguments args = new Arguments();
        args.addArgument("ip", "localhost");
        args.addArgument("port", "8081");
        return args;
    }

    /**
     * 执行runTest()方法后会调用此方法.
     */
    public void teardownTest(JavaSamplerContext arg0) {
    }

    private void Login(String username,String password){
        logger.info("UserName {} 进行登陆...",username);
        loginPlayerInfo.setUserName(username);
        loginPlayerInfo.setPassword(password);
        //从配置中获取游戏用户中心的rl，拼接Http请求地址
        String webGatewayUrl = gameClientConfig.getGameCenterUrl() + CommonField.GAME_CENTER_PATH + MessageCode.USER_LOGIN;
        JSONObject params = new JSONObject();
        params.put("openId", DigestUtils.md5Hex(username));
        params.put("loginType", 1);
        params.put("username", username);
        params.put("password", password);
        //构造请求参数，并发送Http请求登陆，如果username不存在，服务端会创建新的账号，如果已存在，返回已存在的userId
        String result = GameHttpClient.post(webGatewayUrl, params);
        if (StringUtils.isEmpty(result)) {
            logger.info("账号登录失败:{}", result);
            return;
        }
        JSONObject responseJson2 = JSONObject.parseObject(result);
        if (!Integer.valueOf(0).equals(responseJson2.getInteger("code"))) {
            logger.info("账号登录失败:{}", result);
            return;
        }
        JSONObject responseJson = JSONObject.parseObject(result);
        //从返回消息中获取userId和token，记录下来，为以后的命令使用
        long userId = responseJson.getJSONObject("data").getLongValue("userId");
        token = responseJson.getJSONObject("data").getString("token");
        loginPlayerInfo.setUserId(userId);
        loginPlayerInfo.setToken(token);
        //将token验证放在Http的Header里面，以后的命令地请求Http的时候，需要携带，做权限验证
        header = new BasicHeader("user-token", token);
        logger.info("账号登陆成功:{} 自动连接默认区服:ZoneId={}", result, zoneId);
    }
    
    private Boolean connect(){
        try {
            String webGatewayUrl = gameClientConfig.getGameCenterUrl() + CommonField.GAME_CENTER_PATH + MessageCode.SELECT_GAME_GATEWAY;
            SelectGameGatewayParam param = new SelectGameGatewayParam();
            param.setZoneId(zoneId);
            param.setOpenId(loginPlayerInfo.getUserName());//暂代
            param.setToken(token);
            //从用户服务中心选择一个网关，获取网关的连接信息
            String result = GameHttpClient.post(webGatewayUrl, param, header);
            JSONObject responseJson = JSONObject.parseObject(result);
            if (!Integer.valueOf(0).equals(responseJson.getInteger("code"))) {
                logger.info("选择网关失败:{}", result);
                return false;
            }
            GameGatewayInfoMsg gameGatewayInfoMsg = ResponseEntity.parseObject(result, GameGatewayInfoMsg.class).getData();
            loginPlayerInfo.setGameGatewayInfoMsg(gameGatewayInfoMsg);
            gameClientConfig.setRsaPrivateKey(gameGatewayInfoMsg.getRsaPrivateKey());
            gameClientConfig.setGatewayToken(gameGatewayInfoMsg.getToken());
            gameClientConfig.setDefaultGameGatewayHost(gameGatewayInfoMsg.getIp());
            gameClientConfig.setDefaultGameGatewayPort(gameGatewayInfoMsg.getPort());
            logger.info("开始连接网关-{}:{}", gameGatewayInfoMsg.getIp(), gameGatewayInfoMsg.getPort());
            ChannelFuture launch = gameClientBoot.launch();//启动客户端，连接网关
            launch.sync();
            return true;
        } catch (Exception e) {
            logger.error("选择网关失败", e);
        }
        return false;
    }

    public void createPlayer() {
        CreatePlayerParam param = new CreatePlayerParam();
        param.setNickName(username + "");
        param.setZoneId(zoneId);
        String webGatewayUrl = gameClientConfig.getGameCenterUrl() + CommonField.GAME_CENTER_PATH + MessageCode.CREATE_PLAYER;
        //请求创建角色信息
        String result = GameHttpClient.post(webGatewayUrl, param, header);
        logger.info("创建角色返回:{}", result);
        JSONObject responseJson = JSONObject.parseObject(result);
        long playerId = responseJson.getJSONObject("data").getLongValue("playerId");
        loginPlayerInfo.setPlayerId(playerId);
        logger.info("角色PlayerId：{}", playerId);
    }

    public void enterGame() {
        DoEnterGameMsgRequest request = new DoEnterGameMsgRequest();
        gameClientBoot.getChannel().writeAndFlush(request);
        
    }

    private String whoAmI() {
        StringBuilder sb = new StringBuilder();
        sb.append(Thread.currentThread().toString());
        sb.append("@");
        sb.append(Integer.toHexString(hashCode()));
        return sb.toString();
    }
}
