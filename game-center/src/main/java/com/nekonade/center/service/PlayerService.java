package com.nekonade.center.service;

import com.nekonade.common.error.GameErrorException;
import com.nekonade.common.utils.JWTUtil;
import com.nekonade.dao.daos.PlayerDao;
import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.redis.EnumRedisKey;
import com.nekonade.network.param.error.GameCenterError;
import com.nekonade.network.param.http.request.SelectGameGatewayParam;
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
    private PlayerDao playerDao;

    private boolean saveNickNameIfAbsent(String zoneId, String nickName) {
        String key = this.getNickNameRedisKey(zoneId, nickName);
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "0");// value先使用一个默认值
        if (result == null) {
            return false;
        }
        return result;
    }
    private String getNickNameRedisKey(String zoneId,String nickName) {
        String key = EnumRedisKey.PLAYER_NICKNAME.getKey(zoneId + "_" + nickName);
        return key;
    }

    private void updatePlayerIdForNickName(String zoneId, String nickName, long playerId) {
        String key = this.getNickNameRedisKey(zoneId, nickName);
        this.redisTemplate.opsForValue().set(key, String.valueOf(playerId));
    }

    public Player createPlayer(String zoneId, String nickName) {
        boolean saveNickName = this.saveNickNameIfAbsent(zoneId, nickName);
        if (!saveNickName) {// 如果存储失败，抛出错误异常
            throw new GameErrorException.Builder(GameCenterError.NICKNAME_EXIST).message(nickName).build();
        }
        long playerId = this.nextPlayerId(zoneId);//获取一个全局playerId。
        Player player = new Player();
        player.setPlayerId(playerId);
        player.setNickName(nickName);
        player.setLastLoginTime(System.currentTimeMillis());
        player.setCreateTime(player.getLastLoginTime());
        this.updatePlayerIdForNickName(zoneId, nickName, playerId);// 再次更新一下nickName对应的playerId
        playerDao.saveOrUpdate(player, playerId);
        logger.info("创建角色成功,{}", player);
        return player;
    }


    private long nextPlayerId(String zoneId) {
        String key = EnumRedisKey.PLAYER_ID_INCR.getKey(zoneId);
        return redisTemplate.opsForValue().increment(key);
    }
    public String createToken(SelectGameGatewayParam param, String gatewayIp, String publicKey) {
        String username = param.getUsername();
        String openId = param.getOpenId();
        String zoneId = param.getZoneId();
        long userId = param.getUserId();
        long playerId = param.getPlayerId();

        String token = JWTUtil.getUsertoken(openId, userId, playerId, zoneId,username,gatewayIp,publicKey);
        return token;
    }

}
