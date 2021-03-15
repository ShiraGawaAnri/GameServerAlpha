package com.nekonade.game.clienttest;

import com.alibaba.fastjson.JSONObject;
import com.nekonade.common.utils.CommonField;
import com.nekonade.common.utils.GameHttpClient;
import com.nekonade.game.clienttest.common.ClientPlayerInfo;
import com.nekonade.game.clienttest.common.PlayerInfo;
import com.nekonade.game.clienttest.common.RaidBattleInfo;
import com.nekonade.game.clienttest.service.GameClientBoot;
import com.nekonade.game.clienttest.service.GameClientConfig;
import com.nekonade.game.clienttest.service.GameClientInitService;
import com.nekonade.game.clienttest.test.AbstractNekoNadeClientUnitTest;
import com.nekonade.game.clienttest.test.ThreadContainer;
import com.nekonade.network.param.game.message.battle.JoinRaidBattleMsgRequest;
import com.nekonade.network.param.game.message.battle.RaidBattleCardAttackMsgRequest;
import com.nekonade.network.param.game.message.neko.DoDiamondGachaMsgRequest;
import com.nekonade.network.param.game.message.neko.DoEnterGameMsgRequest;
import com.nekonade.network.param.game.message.neko.GetPlayerSelfMsgRequest;
import com.nekonade.network.param.http.MessageCode;
import com.nekonade.network.param.http.request.CreatePlayerParam;
import com.nekonade.network.param.http.request.SelectGameGatewayParam;
import com.nekonade.network.param.http.response.GameGatewayInfoMsg;
import com.nekonade.network.param.http.response.ResponseEntity;
import io.netty.channel.Channel;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/*@SpringBootTest(classes = {GameClientInitService.class, GameClientBoot.class, GameClientConfig.class, DispatchGameMessageService.class, ApplicationContext.class})*/
@SpringBootTest(classes = GameClientTestMain.class)
public class StressTestingTestNG extends AbstractNekoNadeClientUnitTest {

    private final Logger logger = LoggerFactory.getLogger(StressTestingTestNG.class);

    private final String zoneId = "10003";

    public final static Map<Long, RaidBattleInfo> raidBattleInfoMap = new ConcurrentHashMap<>();

    public final static Map<Long, PlayerInfo> playerInfoMap = new ConcurrentHashMap<>();

    @Autowired
    private GameClientBoot gameClientBoot;

    @Autowired
    private GameClientInitService gameClientInitService;

    private final static ThreadLocal<BasicHeader> basicHeaderThreadLocal = new ThreadLocal<>();

    public final static ThreadLocal<GameClientConfig> gameClientConfig = new ThreadLocal<>();


    private String createName() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(RandomUtils.nextInt()).append(System.currentTimeMillis());
        String oridin = UUID.randomUUID().toString() + buffer.toString();
        oridin = DigestUtils.md5Hex(oridin);
        StringBuffer stringBuffer = new StringBuffer();
        int index = RandomUtils.nextInt(4, 9);
        for (int i = 0; i < Math.min(oridin.length(), index); i++) {
            stringBuffer.append(oridin.charAt(i));
        }
        return stringBuffer.toString();
    }

    private BasicHeader Login(String username, String password, ClientPlayerInfo clientPlayerInfo) {
        logger.info("UserName {} 进行登陆...", username);
        clientPlayerInfo.setUserName(username);
        clientPlayerInfo.setPassword(password);
        //从配置中获取游戏用户中心的rl，拼接Http请求地址
        String webGatewayUrl = gameClientConfig.get().getGameCenterUrl() + CommonField.GAME_CENTER_PATH + MessageCode.USER_LOGIN;
        JSONObject params = new JSONObject();
        params.put("openId", DigestUtils.md5Hex(username));
        params.put("loginType", 1);
        params.put("username", username);
        params.put("password", password);
        //构造请求参数，并发送Http请求登陆，如果username不存在，服务端会创建新的账号，如果已存在，返回已存在的userId
        String result = GameHttpClient.post(webGatewayUrl, params);
        if (StringUtils.isEmpty(result)) {
            logger.info("账号登录失败:{}", result);
            return null;
        }
        JSONObject responseJson2 = JSONObject.parseObject(result);
        if (!Integer.valueOf(0).equals(responseJson2.getInteger("code"))) {
            logger.info("账号登录失败:{}", result);
            return null;
        }
        JSONObject responseJson = JSONObject.parseObject(result);
        //从返回消息中获取userId和token，记录下来，为以后的命令使用
        long userId = responseJson.getJSONObject("data").getLongValue("userId");
        String token = responseJson.getJSONObject("data").getString("token");
        clientPlayerInfo.setUserId(userId);
        clientPlayerInfo.setToken(token);
        //将token验证放在Http的Header里面，以后的命令地请求Http的时候，需要携带，做权限验证
        BasicHeader basicHeader = new BasicHeader(CommonField.TOKEN, token);
        basicHeaderThreadLocal.set(basicHeader);
        logger.info("账号登陆成功:{} 自动连接默认区服:ZoneId={}", result, zoneId);
        return basicHeader;
    }

    private Boolean connect(ClientPlayerInfo clientPlayerInfo, Header header) {
        try {
            String webGatewayUrl = gameClientConfig.get().getGameCenterUrl() + CommonField.GAME_CENTER_PATH + MessageCode.SELECT_GAME_GATEWAY;
            SelectGameGatewayParam param = new SelectGameGatewayParam();
            param.setZoneId(zoneId);
            param.setOpenId(clientPlayerInfo.getUserName());//暂代
            param.setToken(clientPlayerInfo.getToken());
            //从用户服务中心选择一个网关，获取网关的连接信息
            String result = GameHttpClient.post(webGatewayUrl, param, header);
            JSONObject responseJson = JSONObject.parseObject(result);
            if (!Integer.valueOf(0).equals(responseJson.getInteger("code"))) {
                logger.info("选择网关失败:{}", result);
                return false;
            }
            GameGatewayInfoMsg gameGatewayInfoMsg = ResponseEntity.parseObject(result, GameGatewayInfoMsg.class).getData();
            clientPlayerInfo.setGameGatewayInfoMsg(gameGatewayInfoMsg);
            GameClientConfig config = StressTestingTestNG.gameClientConfig.get();
            config.setRsaPrivateKey(gameGatewayInfoMsg.getRsaPrivateKey());
            config.setGatewayToken(gameGatewayInfoMsg.getToken());
            config.setDefaultGameGatewayHost(gameGatewayInfoMsg.getIp());
            config.setDefaultGameGatewayPort(gameGatewayInfoMsg.getPort());
            logger.info("开始连接网关-{}:{}", gameGatewayInfoMsg.getIp(), gameGatewayInfoMsg.getPort());
            CompletableFuture<Boolean> waite = gameClientBoot.launch(clientPlayerInfo.getPlayerId(), config);
            waite.get();
            return true;
        } catch (Exception e) {
            logger.error("选择网关失败", e);
        }
        return false;
    }

    public void createPlayer(ClientPlayerInfo clientPlayerInfo, Header header) {
        CreatePlayerParam param = new CreatePlayerParam();
        param.setNickName(clientPlayerInfo.getUserName());
        param.setZoneId(zoneId);
        GameClientConfig config = StressTestingTestNG.gameClientConfig.get();
        String webGatewayUrl = config.getGameCenterUrl() + CommonField.GAME_CENTER_PATH + MessageCode.CREATE_PLAYER;
        //请求创建角色信息
        String result = GameHttpClient.post(webGatewayUrl, param, header);
        logger.info("创建角色返回:{}", result);
        JSONObject responseJson = JSONObject.parseObject(result);
        long playerId = responseJson.getJSONObject("data").getLongValue("playerId");
        clientPlayerInfo.setPlayerId(playerId);
        logger.info("角色PlayerId：{}", playerId);
    }

    private void enterGame() {
        ClientPlayerInfo clientPlayerInfo = ThreadContainer.getClientPlayerInfo().get();
        long playerId = clientPlayerInfo.getPlayerId();
        DoEnterGameMsgRequest request = new DoEnterGameMsgRequest();
        gameClientBoot.getChannel(playerId).writeAndFlush(request);
    }

    private void getSelfInfo(){
        ClientPlayerInfo clientPlayerInfo = ThreadContainer.getClientPlayerInfo().get();
        long playerId = clientPlayerInfo.getPlayerId();
        GetPlayerSelfMsgRequest getPlayerSelfMsgRequest = new GetPlayerSelfMsgRequest();
        gameClientBoot.getChannel(playerId).writeAndFlush(getPlayerSelfMsgRequest);
    }

    private void gacha() {
        ClientPlayerInfo clientPlayerInfo = ThreadContainer.getClientPlayerInfo().get();
        long playerId = clientPlayerInfo.getPlayerId();
        DoDiamondGachaMsgRequest request = new DoDiamondGachaMsgRequest();
        request.getBodyObj().setGachaPoolsId("GachaPoolAlpha0001");
        gameClientBoot.getChannel(playerId).writeAndFlush(request);
    }

    private void joinRaidBattle(String raidId) {
        ClientPlayerInfo clientPlayerInfo = ThreadContainer.getClientPlayerInfo().get();
        long playerId = clientPlayerInfo.getPlayerId();
        JoinRaidBattleMsgRequest request = new JoinRaidBattleMsgRequest();
        request.getHeader().getAttribute().setRaidId(raidId);
        request.getBodyObj().setRaidId(raidId);
        gameClientBoot.getChannel(playerId).writeAndFlush(request);
    }

    private void raidBattleAttack(RaidBattleInfo raidBattleInfo) {
        ClientPlayerInfo clientPlayerInfo = ThreadContainer.getClientPlayerInfo().get();
        long playerId = clientPlayerInfo.getPlayerId();
        RaidBattleCardAttackMsgRequest request = new RaidBattleCardAttackMsgRequest();
        request.getHeader().getAttribute().setRaidId(raidBattleInfo.getRaidId());
        request.getBodyObj().setCharaId("TEST_CHARA_0004");
        gameClientBoot.getChannel(playerId).writeAndFlush(request);
    }

    private String whoAmI() {
        StringBuilder sb = new StringBuilder();
        sb.append(Thread.currentThread().toString());
        sb.append("@");
        sb.append(Integer.toHexString(hashCode()));
        return sb.toString();
    }

    @BeforeTest
    public void init() {
        logger.info("Before Test");
    }

    @BeforeClass
    public void initBeforeTest() {
        logger.info("Before TestMethod");
        gameClientInitService.testInit("com.nekonade.game.clienttest");
    }

    @Test(description = "MyTest1___", threadPoolSize = 100,invocationCount = 100)
    public void MyTest1() {
        PlayerInfo playerInfo = ThreadContainer.getPlayerInfo().get();
        ClientPlayerInfo clientPlayerInfo = ThreadContainer.getClientPlayerInfo().get();
        RaidBattleInfo raidBattleInfo = ThreadContainer.getRaidBattleInfo().get();

        gameClientConfig.set(new GameClientConfig());

        String name = createName();
        String username = name;
        String password = name;
        try {
            BasicHeader header = Login(username, password, clientPlayerInfo);
            createPlayer(clientPlayerInfo, header);
            long playerId = clientPlayerInfo.getPlayerId();
            if (playerId == 0) {
                throw new Exception("创建角色失败");
            }
            playerInfoMap.put(clientPlayerInfo.getPlayerId(), playerInfo);
            raidBattleInfoMap.put(clientPlayerInfo.getPlayerId(), raidBattleInfo);
            /*RaidBattleInfo raidBattleInfo = new RaidBattleInfo();
            PlayerInfo playerInfo = new PlayerInfo();
            raidBattleInfoMap.put(playerId,raidBattleInfo);
            playerInfoMap.put(playerId,playerInfo);*/

            Boolean connect = connect(clientPlayerInfo, header);
            if (!connect) {
                throw new Exception("连接失败");
            }
            Channel channel = gameClientBoot.getChannel(playerId);
            if (channel == null) {
                logger.error("线程{}未取得channel", Thread.currentThread().getName());
                throw new RuntimeException("channel初始化失败");
            }
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Thread.sleep(1500);
            enterGame();
            Future<Boolean> waitEnter = executorService.submit(() -> {
                while (!playerInfo.isEntered()) {
                    Thread.sleep(100);
                }
                return true;
            });
            try {
                waitEnter.get(3, TimeUnit.SECONDS);
            }catch (Exception e){
                throw new RuntimeException("进入游戏超时");
            }
            Thread.sleep(1500);
            getSelfInfo();
            gacha();
            Future<Boolean> waitGacha = executorService.submit(() -> {
                while (playerInfo.getCharacters().size() == 0) {
                    Thread.sleep(100);
                }
                return true;
            });
            try {
                waitGacha.get(3, TimeUnit.SECONDS);
            }catch (Exception e){
                logger.info("playerInfo.getCharacters() {}",playerInfo.getCharacters().size());
                throw new Exception("抽卡池超时");
            }
            for (int i = 0; i < 5; i++) {
                Thread.sleep(500);
                gacha();
            }
            String raidId = "6da4089486c143c4e9bb0274d7f8cf7c";
            joinRaidBattle(raidId);
            Future<Boolean> waitJoin = executorService.submit(() -> {
                while (raidBattleInfo.getRaidId() == null) {
                    Thread.sleep(1000);
                }
                return true;
            });
            try {
                waitJoin.get(20, TimeUnit.SECONDS);
            }catch (Exception e){
                throw new RuntimeException("进入战斗超时");
            }
            while (channel.isActive() && channel.isOpen()) {
                raidBattleAttack(raidBattleInfo);
                Thread.sleep(1000);
            }
            //raidBattleAttack(raidBattleInfo);
        } catch (Exception e) {
            logger.error("UserName:{} PlayerID:{} 超时/错误:{}", username, (clientPlayerInfo == null ? null : clientPlayerInfo.getPlayerId()), e.getMessage());
        }
    }
}
