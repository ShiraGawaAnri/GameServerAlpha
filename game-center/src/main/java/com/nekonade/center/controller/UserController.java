package com.nekonade.center.controller;

import com.nekonade.center.messages.GameCenterError;
import com.nekonade.center.messages.request.CreatePlayerParam;
import com.nekonade.center.messages.request.LoginParam;
import com.nekonade.center.messages.request.SelectGameGatewayParam;
import com.nekonade.center.messages.response.GameGatewayInfoMsg;
import com.nekonade.center.messages.response.VoPlayerBasic;
import com.nekonade.center.messages.response.VoUserAccount;
import com.nekonade.center.service.GameGatewayService;
import com.nekonade.center.service.PlayerService;
import com.nekonade.center.service.UserLoginService;
import com.nekonade.center.service.model.GameGatewayInfo;
import com.nekonade.common.GameConstants;
import com.nekonade.common.errors.TokenException;
import com.nekonade.common.utils.GameBeanUtils;
import com.nekonade.common.utils.IpUtils;
import com.nekonade.common.utils.JWTUtil;
import com.nekonade.common.utils.RSAUtils;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.UserAccount;
import com.nekonade.network.message.errors.GameErrorException;
import com.nekonade.network.message.errors.IServerError;
import com.nekonade.network.message.web.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private GameGatewayService gameGatewayService;
    @Autowired
    private PlayerService playerService;

    
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("login")
    public ResponseEntity<VoUserAccount> login(@RequestBody LoginParam loginParam, HttpServletRequest request) {
        loginParam.checkParam();// 检测请求参数的合法性
        String loginIp = IpUtils.getIpAddr(request);
        loginParam.setLoginIp(loginIp);
        UserAccount userAccount = null;
        if (loginParam.isUserNameLogin()) {
            userAccount = userLoginService.loginByUserName(loginParam);
        } else {
            IServerError serverError = userLoginService.verfiySdkToken(loginParam.getOpenId(), loginParam.getToken());
            if (serverError != null) {// 请求第三方，验证登陆信息的正确性
                throw GameErrorException.newBuilder(serverError).build();
            }
            //暂时不实现。
            throw new IllegalArgumentException("目前不支持此种方式登录注册");
        }
        userLoginService.updateUserAccountExpire(userAccount);
        VoUserAccount voUserAccount = new VoUserAccount();
        GameBeanUtils.shallowCopy(userAccount, voUserAccount);
        String token = userLoginService.createUserToken(userAccount, loginParam.getLoginType());
        voUserAccount.setToken(token);// 这里使用JWT生成Token
        logger.debug("user {} 登陆成功", userAccount.getUserName());
        return new ResponseEntity<VoUserAccount>(voUserAccount);
    }

    @PostMapping("createPlayer")
    public ResponseEntity<VoPlayerBasic> createPlayer(@RequestBody CreatePlayerParam param, HttpServletRequest request) throws TokenException {
        param.checkParam();
        String userToken = request.getHeader(GameConstants.USER_TOKEN);
        JWTUtil.TokenContent tokenContent = JWTUtil.getTokenContent(userToken);
        long userId =tokenContent.getUserId();
        UserAccount userAccount = userLoginService.getUserAccountByUserId(userId).orElse(null);
        if(userAccount == null) {
            throw GameErrorException.newBuilder(GameCenterError.USER_NOT_EXIST).build();
        }
        Player player = playerService.createPlayer(param.getNickName());
        userAccount.getPlayers().add(player);
        userLoginService.updateUserAccount(userAccount);
        VoPlayerBasic playerBasic = new VoPlayerBasic();
        GameBeanUtils.shallowCopy(player, playerBasic);
        ResponseEntity<VoPlayerBasic> response = new ResponseEntity<VoPlayerBasic>(playerBasic);
        return response;
    }

    @PostMapping("selectGameGateway")
    public Object selectGameGateway(@RequestBody SelectGameGatewayParam param, HttpServletRequest request) throws Exception {
        param.checkParam();
        long playerId = param.getPlayerId();
        GameGatewayInfo gameGatewayInfo = gameGatewayService.getGameGatewayInfo(playerId);
        GameGatewayInfoMsg gameGatewayInfoMsg = new GameGatewayInfoMsg(gameGatewayInfo.getId(), gameGatewayInfo.getIp(), gameGatewayInfo.getPort());
        Map<String, Object> keyPair = RSAUtils.genKeyPair();// 生成rsa的公钥和私钥
        byte[] publickKeyBytes = RSAUtils.getPublicKey(keyPair);// 获取公钥
        String publickKey = Base64Utils.encodeToString(publickKeyBytes);// 为了方便传输，对bytes数组进行一下base64编码
        String userToken = request.getHeader(GameConstants.USER_TOKEN);
        String token = playerService.createToken(userToken,playerId, gameGatewayInfo.getIp(), publickKey);// 根据这些参数生成token
        gameGatewayInfoMsg.setToken(token);
        byte[] privateKeyBytes = RSAUtils.getPrivateKey(keyPair);
        String privateKey = Base64Utils.encodeToString(privateKeyBytes);
        gameGatewayInfoMsg.setRsaPrivateKey(privateKey);// 给客户端返回私钥
        logger.debug("player {} 获取游戏网关信息成功：{}", playerId, gameGatewayInfoMsg);
        ResponseEntity<GameGatewayInfoMsg> responseEntity = new ResponseEntity<>(gameGatewayInfoMsg);
        return responseEntity;
    }

}
