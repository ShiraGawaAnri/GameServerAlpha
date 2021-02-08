package com.nekonade.dao.daos;

import com.nekonade.dao.db.entity.Player;
import com.nekonade.dao.db.repository.PlayerRepository;
import com.nekonade.dao.redis.EnumRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlayerDao extends AbstractDao<Player, Long> {
    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public EnumRedisKey getRedisKey() {
        return EnumRedisKey.PLAYER_INFO;
    }

    @Override
    protected MongoRepository<Player, Long> getMongoRepository() {
        return playerRepository;
    }

    @Override
    protected Class<Player> getEntityClass() {
        return Player.class;
    }

    public String findPlayerFromRedis(long playerId) {
        String key = this.getRedisKey().getKey(String.valueOf(playerId));
        return key.equals("") ? null : stringRedisTemplate.opsForValue().get(key);
    }
}
