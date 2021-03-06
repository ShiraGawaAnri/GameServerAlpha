package com.nekonade.center.controller;

import com.nekonade.center.dataconfig.GameGatewayInfo;
import com.nekonade.center.service.GameGatewayService;
import com.nekonade.center.service.PlayerService;
import com.nekonade.center.service.UserLoginService;
import com.nekonade.common.constcollections.EnumCollections;
import com.nekonade.common.error.exceptions.GameErrorException;
import com.nekonade.common.error.IServerError;
import com.nekonade.common.utils.CommonField;
import com.nekonade.common.utils.JWTUtil;
import com.nekonade.common.utils.RSAUtils;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.entity.UserAccount;
import com.nekonade.network.param.http.MessageCode;
import com.nekonade.network.param.http.request.CreatePlayerParam;
import com.nekonade.network.param.http.request.LoginParam;
import com.nekonade.network.param.http.request.SelectGameGatewayParam;
import com.nekonade.network.param.http.response.GameGatewayInfoMsg;
import com.nekonade.network.param.http.response.LoginResult;
import com.nekonade.network.param.http.response.ResponseEntity;
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
import java.util.Optional;

@RestController
@RequestMapping(CommonField.GAME_CENTER_PATH)
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private GameGatewayService gameGatewayService;

    @Autowired
    private PlayerService playerService;


//    @PostMapping("login")
//    public ResponseEntity<VoUserAccount> login(@RequestBody LoginParam loginParam, HttpServletRequest request) {
//        loginParam.checkParam();// ??????????????????????????????
//        String loginIp = IpUtils.getIpAddr(request);
//        loginParam.setLoginIp(loginIp);
//        UserAccount userAccount = null;
//        if (loginParam.isUserNameLogin()) {
//            userAccount = userLoginService.loginByUserName(loginParam);
//        } else {
//            IServerError serverError = userLoginService.verfiySdkToken(loginParam.getOpenId(), loginParam.getToken());
//            if (serverError != null) {// ????????????????????????????????????????????????
//                throw GameErrorException.newBuilder(serverError).build();
//            }
//            //??????????????????
//            throw new IllegalArgumentException("???????????????????????????????????????");
//        }
//        userLoginService.updateUserAccountExpire(userAccount);
//        VoUserAccount voUserAccount = new VoUserAccount();
//        GameBeanUtils.shallowCopy(userAccount, voUserAccount);
//        String token = userLoginService.createUserToken(userAccount, loginParam.getLoginType());
//        voUserAccount.setToken(token);// ????????????JWT??????Token
//        logger.debug("user {} ????????????", userAccount.getUserName());
//        return new ResponseEntity<VoUserAccount>(voUserAccount);
//    }
//
//    @PostMapping("createPlayer")
//    public ResponseEntity<VoPlayerBasic> createPlayer(@RequestBody CreatePlayerParam param, HttpServletRequest request) throws TokenException {
//        param.checkParam();
//        String userToken = request.getHeader(CommonField.TOKEN);
//        JWTUtil.TokenBody tokenBody = JWTUtil.getTokenContent(userToken);
//        long userId = tokenBody.getUserId();
//        UserAccount userAccount = userLoginService.getUserAccountByUserId(userId).orElse(null);
//        if (userAccount == null) {
//            throw GameErrorException.newBuilder(GameCenterError.USER_NOT_EXIST).build();
//        }
//        Player player = playerService.createPlayer(param.getNickName());
//        userAccount.getPlayers().add(player);
//        userLoginService.updateUserAccount(userAccount);
//        VoPlayerBasic playerBasic = new VoPlayerBasic();
//        GameBeanUtils.shallowCopy(player, playerBasic);
//        ResponseEntity<VoPlayerBasic> responseEntity = new ResponseEntity<VoPlayerBasic>(playerBasic);
//        return responseEntity;
//    }
//
//    @PostMapping("selectGameGateway")
//    public Object selectGameGateway(@RequestBody SelectGameGatewayParam param, HttpServletRequest request) throws Exception {
//        param.checkParam();
//        long playerId = param.getPlayerId();
//        GameGatewayInfo gameGatewayInfo = gameGatewayService.getGameGatewayInfo(playerId);
//        GameGatewayInfoMsg gameGatewayInfoMsg = new GameGatewayInfoMsg(gameGatewayInfo.getId(), gameGatewayInfo.getIp(), gameGatewayInfo.getPort());
//        Map<String, Object> keyPair = RSAUtils.genKeyPair();// ??????rsa??????????????????
//        byte[] publicKeyBytes = RSAUtils.getPublicKey(keyPair);// ????????????
//        String publicKey = Base64Utils.encodeToString(publicKeyBytes);// ????????????????????????bytes??????????????????base64??????
//        String userToken = request.getHeader(CommonField.TOKEN);
//        String token = playerService.createToken(userToken, playerId, gameGatewayInfo.getIp(), publicKey);// ????????????????????????token
//        gameGatewayInfoMsg.setToken(token);
//        byte[] privateKeyBytes = RSAUtils.getPrivateKey(keyPair);
//        String privateKey = Base64Utils.encodeToString(privateKeyBytes);
//        gameGatewayInfoMsg.setRsaPrivateKey(privateKey);// ????????????????????????
//        logger.debug("player {} ?????????????????????????????????{}", playerId, gameGatewayInfoMsg);
//        ResponseEntity<GameGatewayInfoMsg> responseEntity = new ResponseEntity<>(gameGatewayInfoMsg);
//        return responseEntity;
//    }

    @PostMapping(MessageCode.USER_LOGIN)
    public ResponseEntity<LoginResult> login(@RequestBody LoginParam loginParam) {
        loginParam.checkParam();//??????????????????????????????
        IServerError serverError = userLoginService.verfiySdkToken(loginParam.getOpenId(), loginParam.getToken());
        if (serverError != null) {//????????????????????????????????????????????????
            throw GameErrorException.newBuilder(serverError).build();
        }
        UserAccount userAccount = userLoginService.login(loginParam);
        LoginResult loginResult = new LoginResult();
        loginResult.setUserId(userAccount.getUserId());
        String token = JWTUtil.getUserTokenV2(userAccount.getOpenId(), userAccount.getUserId(), userAccount.getUsername());
        loginResult.setToken(token);// ????????????JWT??????Token
        logger.info("user {} ????????????", userAccount);
        return new ResponseEntity<>(loginResult);
    }

    @PostMapping(MessageCode.SELECT_GAME_GATEWAY)
    public Object selectGameGateway(@RequestBody SelectGameGatewayParam param) throws Exception {
        param.checkParam();
        JWTUtil.TokenBody tokenBody = JWTUtil.getTokenBodyV2(param.getToken());
        long userId = tokenBody.getUserId();
        Optional<UserAccount> op = userLoginService.getUserAccountByUserId(userId);
        if (op.isEmpty()) {
            throw GameErrorException.newBuilder(EnumCollections.CodeMapper.GameCenterError.ILLEGAL_LOGIN_TYPE).build();
        }
        UserAccount userAccount = op.get();
        UserAccount.ZonePlayerInfo zonePlayerInfo = userAccount.getZonePlayerInfo().get(param.getZoneId());
        long playerId = -1;
        if (zonePlayerInfo == null) {
            //????????????????????????????????????????????????????????????????????????
            if (userAccount.getUserId() > 0) {
                throw GameErrorException.newBuilder(EnumCollections.CodeMapper.GameCenterError.NOT_CREATEPLAYER_ERROR).build();
            }
        } else {
            playerId = zonePlayerInfo.getPlayerId();
        }
        param.setPlayerId(playerId);
        GameGatewayInfo gameGatewayInfo = gameGatewayService.getGameGatewayInfo(playerId);
        GameGatewayInfoMsg gameGatewayInfoMsg = new GameGatewayInfoMsg(gameGatewayInfo.getId(), gameGatewayInfo.getIp(), gameGatewayInfo.getPort());
        Map<String, Object> keyPair = RSAUtils.genKeyPair();// ??????rsa??????????????????
        byte[] publicKeyBytes = RSAUtils.getPublicKey(keyPair);// ????????????
        String publicKey = Base64Utils.encodeToString(publicKeyBytes);// ????????????????????????bytes??????????????????base64??????
        String token = playerService.createToken(param, gameGatewayInfo.getIp(), publicKey);// ????????????????????????token
        gameGatewayInfoMsg.setToken(token);
        byte[] privateKeyBytes = RSAUtils.getPrivateKey(keyPair);
        String privateKey = Base64Utils.encodeToString(privateKeyBytes);
        gameGatewayInfoMsg.setRsaPrivateKey(privateKey);// ????????????????????????
        logger.debug("player {} ?????????????????????????????????{}", playerId, gameGatewayInfoMsg);
        return new ResponseEntity<>(gameGatewayInfoMsg);
    }

    @PostMapping(MessageCode.CREATE_PLAYER)
    public ResponseEntity<UserAccount.ZonePlayerInfo> createPlayer(@RequestBody CreatePlayerParam param, HttpServletRequest request) {
        param.checkParam();
//        String token = request.getHeader("token");//???http??????????????????token??????
//        if (token == null) {
//            throw GameErrorException.newBuilder(GameCenterError.TOKEN_FAILED).build();
//        }
//        TokenBody tokenBody;
//        try {
//            tokenBody = JWTUtil.getTokenBody(token);//????????????token?????????????????????
//        } catch (TokenException e) {
//            throw GameErrorException.newBuilder(GameCenterError.TOKEN_FAILED).build();
//        }
//        String openId = tokenBody.getOpenId();
        //???????????????????????????????????????????????????openId??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        long userId = userLoginService.getUserIdFromHeader(request);
        Optional<UserAccount> op = userLoginService.getUserAccountByUserId(userId);
        if(op.isEmpty()){
            throw GameErrorException.newBuilder(EnumCollections.CodeMapper.GameCenterError.USER_ACCOUNT_NOT_FOUND).build();
        }
        UserAccount userAccount = op.get();
        String zoneId = param.getZoneId();
        UserAccount.ZonePlayerInfo zoneInfo = userAccount.getZonePlayerInfo().get(zoneId);
        boolean createPlayer = false;
        if (zoneInfo == null) {
            Player player = playerService.createPlayer(param.getZoneId(), param.getNickName());
            zoneInfo = new UserAccount.ZonePlayerInfo(player.getPlayerId(), System.currentTimeMillis());
            userAccount.getZonePlayerInfo().put(zoneId, zoneInfo);
            userLoginService.updateUserAccount(userAccount);
            createPlayer = true;
        }
        ResponseEntity<UserAccount.ZonePlayerInfo> response = new ResponseEntity<>(zoneInfo);
        if (!createPlayer) {
            response.setCode(EnumCollections.CodeMapper.GameCenterError.DUPLICATE_CREATEPLAYER_ERROR.getErrorCode());
            response.setErrorMsg(EnumCollections.CodeMapper.GameCenterError.DUPLICATE_CREATEPLAYER_ERROR.getErrorDesc());
        }
        return response;
    }
}
