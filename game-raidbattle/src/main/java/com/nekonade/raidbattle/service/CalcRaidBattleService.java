package com.nekonade.raidbattle.service;


import com.nekonade.common.error.GameNotifyException;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.dao.daos.CardsDbDao;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.dao.db.entity.data.ActiveSkillsDB;
import com.nekonade.dao.db.entity.data.CardsDB;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CalcRaidBattleService {

    @Autowired
    private CardsDbDao cardsDbDao;

    public void calcCardAttack(RaidBattleManager dataManager, RaidBattle.Player actionPlayer, String cardId, int targetPos, List<Integer> selectCharaPos, long turn) {
        //使用DB来替代
        /*CardsDB cardsDB = cardsDbDao.findCardsDB(cardId);
        if(cardsDB == null){
            throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleAttackInvalidParam).build();
        }*/
        Map<String, CardsDB> allCardsDB = cardsDbDao.findAllCardsDB();
        //随机使用卡片
        List<CardsDB> cardsDBS = new ArrayList<>(allCardsDB.values());
        if(cardsDBS.size() == 0){
            throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleAttackInvalidParam).build();
        }
        Collections.shuffle(cardsDBS);
        CardsDB cardsDB = cardsDBS.get(0);
        ActiveSkillsDB cardSkill = cardsDB.getCardSkill();
        //得到使用的SkillId
        String skillId = cardSkill.getId();
        int skillType = cardSkill.getType();
        int miss = 0;
        int baseDamage = 0;
        //获取使用的对象
        switch (skillId){
            default:
                throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleAttackInvalidParam).build();
            case "BaseSkill_NoAction":
                break;
            case "BaseSkill_Attack1":
                break;
            case "BaseSkill_Attack2":
                break;
            case "BaseSkill_HeavyAttack1":
                break;
            case "BaseSkill_SeriesAttack1":
                break;
            case "BaseSkill_ReduceDefenceAttack1":
                break;
            case "BuffSkill_BuffAtk1":
                break;
            case "BuffSkill_BuffAtk2":
                break;
        }
        if(miss == 1) baseDamage = 0;

    }

    public boolean calcSkillMiss(){
        return false;
    }
}
