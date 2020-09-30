package com.nekonade.center.service;

import com.nekonade.center.logicconfig.GameCenterConfig;
import com.nekonade.center.logicconfig.GameCenterRedisKeyConifg;
import com.nekonade.center.messages.GameCenterError;
import com.nekonade.common.errors.TokenException;
import com.nekonade.common.utils.JWTUtil;
import com.nekonade.dao.daos.PlayerDao;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.network.message.errors.GameErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private GameCenterConfig gameCenterConfig;
    @Autowired
    private PlayerDao playerDao;

    private boolean saveNickNameIfAbsent( String nickName) {
        String key = this.getNickNameRedisKey(nickName);
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "0");// value先使用一个默认值
        if (result == null) {
            return false;
        }
        return result;
    }
    private String getNickNameRedisKey(String nickName) {
        String key = GameCenterRedisKeyConifg.PLAYER_NICKNAME.getKey(nickName);
        return key;
    }

    private void updatePlayerIdForNickName( String nickName, long playerId) {
        String key = this.getNickNameRedisKey(nickName);
        this.redisTemplate.opsForValue().set(key, String.valueOf(playerId));
    }

    public Player createPlayer(String nickName) {
        boolean saveNickName = this.saveNickNameIfAbsent(nickName);
        if (!saveNickName) {// 如果存储失败，抛出错误异常
            throw new GameErrorException.Builder(GameCenterError.NICKNAME_EXIST).message(nickName).build();
        }
        long playerId = this.nextPlayerId();//获取一个全局playerId。
        Player player = new Player();
        player.setNewCreate(true);
        player.setPlayerId(playerId);
        player.setNickName(nickName);
        player.setLastLoginTime(System.currentTimeMillis());
        player.setCreateTime(player.getLastLoginTime());
        this.updatePlayerIdForNickName(nickName, playerId);// 再次更新一下nickName对应的playerId
        playerDao.saveOrUpdate(player, playerId);
        logger.info("创建角色成功,{}", player);
        return player;
    }


    private long nextPlayerId() {
        String key = GameCenterRedisKeyConifg.PLAYER_ID_INCR.getKey();
        return redisTemplate.opsForValue().increment(key);
    }
    public String createToken(String userToken,long playerId,String gatewayIp,String publicKey) throws TokenException {

        JWTUtil.TokenContent tokenContent = JWTUtil.getTokenContent(userToken);
        tokenContent.setPublicKey(publicKey);
        tokenContent.setPlayerId(playerId);
        tokenContent.setGatewayIp(gatewayIp);
        String token = JWTUtil.createToken(tokenContent, Duration.ofDays(gameCenterConfig.getPlayerTokenExpire()));
        return token;
    }
    public Optional<Player> getPlayerByPlayerId(long playerId) {
        Optional<Player> playerOp = playerDao.findByIdFromCacheOrLoader(playerId);
        return playerOp;
    }

}
