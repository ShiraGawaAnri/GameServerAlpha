package com.nekonade.network.message.manager;

import com.nekonade.dao.db.entity.Character;
import com.nekonade.dao.db.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ConcurrentHashMap;

public class CharacterManager {//英雄管理类

    private static final Logger logger = LoggerFactory.getLogger(CharacterManager.class);
    private final PlayerManager playerManager;
    private final ApplicationContext context;
    private final ConcurrentHashMap<String, Character> characters;//英雄数据集合对象
    private final Player player;//角色对象，有些日志和事件记录需要这个对象。

    public CharacterManager(PlayerManager playerManager) {
        this.context = playerManager.getContext();
        this.playerManager = playerManager;
        this.player = playerManager.getPlayer();
        this.characters = player.getCharacters();
    }

    public void addChara(Character character) {
        this.characters.put(character.getCharaId(), character);
    }

    public Character getChara(String heroId) {
        Character character = this.characters.get(heroId);
        if (character == null) {
            logger.debug("player {} 没有英雄:{}", player.getPlayerId(), heroId);
        }
        return character;
    }

}
