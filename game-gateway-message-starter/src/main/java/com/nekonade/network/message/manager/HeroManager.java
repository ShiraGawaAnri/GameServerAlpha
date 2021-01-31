package com.nekonade.network.message.manager;

import com.nekonade.common.error.GameErrorException;
import com.nekonade.dao.db.entity.Hero;
import com.nekonade.dao.db.entity.HeroSkill;
import com.nekonade.dao.db.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class HeroManager {//英雄管理类
    private static final Logger logger = LoggerFactory.getLogger(HeroManager.class);
    private final ConcurrentHashMap<String, Hero> heroMap;//英雄数据集合对象
    private final Player player;//角色对象，有些日志和事件记录需要这个对象。
    
    public HeroManager(Player player) {
        this.player = player;
        this.heroMap = player.getHerosMap();
    }
    public void addHero(Hero hero) {
        this.heroMap.put(hero.getHeroId(), hero);
    }
    public Hero getHero(String heroId) {
        Hero hero = this.heroMap.get(heroId);
        if(hero == null) {
            logger.debug("player {} 没有英雄:{}",player.getPlayerId(),heroId);
        }
        return hero;
    }
    private HeroSkill getHeroKill(Hero hero, String skillId) {
        HeroSkill heroSkill = hero.getSkillMap().get(skillId);
        if(heroSkill == null) {
            logger.debug("player {} 的英雄 {} 的技能{}不存在",player.getPlayerId(),hero.getHeroId(),skillId);
        }
        return heroSkill;
    }
    public boolean isSkillArrivalMaxLevel(String heroId,String skillId) {
        Hero hero = this.getHero(heroId);
        HeroSkill heroSkill = this.getHeroKill(hero, skillId);
        int skillLv = heroSkill.getLevel();
        //根据等级判断是否达到最大等级
        return skillLv >= 100;
    }
    
    public void checkHeroExist(String heroId) {
        if(!this.heroMap.containsKey(heroId)) {
            throw GameErrorException.newBuilder(GameErrorCode.HeroNotExist).build();
        }
    }
    public void checkHadEquipWeapon(Hero hero) {
        if(hero.getWeaponId() != null) {
            throw GameErrorException.newBuilder(GameErrorCode.HeroHadEquipedWeapon).build();
        }
    }
    public void checkHeroLevelEnough(int heroLevel,int needLevel) {
        if(heroLevel < needLevel) {
            throw GameErrorException.newBuilder(GameErrorCode.HeroLevelNotEnough).message("需要等级：{}",20).build();
        }
    }
    
}
