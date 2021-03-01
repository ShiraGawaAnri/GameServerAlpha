package com.nekonade.raidbattle.service;


import com.nekonade.common.error.GameNotifyException;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.dao.daos.CardsDbDao;
import com.nekonade.dao.db.EnumEntityDB;
import com.nekonade.dao.db.entity.RaidBattle;
import com.nekonade.dao.db.entity.data.ActiveSkillsDB;
import com.nekonade.dao.db.entity.data.CardsDB;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class CalcRaidBattleService {


    @Getter
    @Setter
    private class Damage{

        private double totalDamage = 0L;


        public void addDamage(long damage){
            this.totalDamage += damage;
        }

        public void addDamage(double damage){
            this.totalDamage += damage;
        }

        public void addDamage(int damage){
            this.totalDamage += damage;
        }
    }

    @Autowired
    private CardsDbDao cardsDbDao;

    public void calcCardAttack(RaidBattleManager dataManager, RaidBattle.Player actionPlayer, RaidBattle.Player.Character character, String cardId, int targetPos, List<Integer> selectCharaPos, long turn) {
        //使用DB来替代
        /*CardsDB cardsDB = cardsDbDao.findCardsDB(cardId);
        if(cardsDB == null){
            throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleAttackInvalidParam).build();
        }*/
        CopyOnWriteArrayList<RaidBattle.Enemy> enemies = dataManager.getRaidBattle().getEnemies();



        Map<String, CardsDB> allCardsDB = cardsDbDao.findAllCardsDB();
        //随机使用卡片
        List<CardsDB> cardsDBS = new ArrayList<>(allCardsDB.values());
        if(cardsDBS.size() == 0){
            throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleAttackInvalidParam).build();
        }
        Collections.shuffle(cardsDBS);
        CardsDB cardsDB = cardsDBS.get(0);

        //不满足card基本要求无法触发技能
        //.............

        ActiveSkillsDB cardSkill = cardsDB.getCardSkill();

        ActiveSkillsDB.Flags flags = cardSkill.getFlags();


        //得到使用的SkillId
        String skillId = cardSkill.getId();
        //先判断角色是否可以攻击
        ConcurrentHashMap<String, Object> debuffs = actionPlayer.getDebuffs();
        //特定debuff下无法行动
        if(debuffs != null && debuffs.size() > 0){
            //.........
        }


        //不满足一定条件无法释放
        List<ActiveSkillsDB.Requires> requires = cardSkill.getRequires();

        if(requires != null && requires.size() > 0){
            //.........
        }

        //是否必须装备什么
        Object equipment = cardSkill.getEquipment();
        if(equipment != null){
            //.........
        }

        //是否要求处于某个buff/debuff下
        List<ActiveSkillsDB.Status> status = cardSkill.getStatus();
        if(status != null && status.size() > 0){
            //........
        }

        //是否要求处于某个特殊场景
        int state = cardSkill.getState();
        if(state != EnumEntityDB.EnumNumber.RaidBattle_In_State_None.getValue()){
            //.......
        }

        //满足以上条件时才可使用

        //在这里扣除一些特殊消耗 如HP,道具等
        //......

        ActiveSkillsDB.DamageFlags damageFlags = cardSkill.getDamageFlags();

        int skillType = cardSkill.getType();
        int miss = 0;
        double baseDamage = 0;
        int value1 = cardsDB.getValue1();
        int value2 = cardsDB.getValue2();
        int value3 = cardsDB.getValue3();
        int value4 = cardsDB.getValue4();
        double skillRatio = 0;
        double fixDamage = 0;//固定附加伤害
        Damage damage = new Damage();

        //由人物计算出baseAtk
        Integer atk = character.getAtk();

        baseDamage = atk;

        //遍历角色身上装备,得出所有增加技能倍率的值并放进hashmap中
        Map<String,Double> skillAddRatioMap = new HashMap<>();
        //...........

        //计算技能倍率
        switch (skillId){
            default:
                throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleAttackInvalidParam).build();
            case "BaseSkill_NoAction":
                skillRatio = 0;
                break;
            case "BaseSkill_Attack1":
                skillRatio = value1 == 0 ? 100 : value1;
                break;
            case "BaseSkill_Attack2":
                skillRatio = value1 == 0 ? 120 : value1;
                break;
            case "BaseSkill_HeavyAttack1":
                skillRatio = value1 == 0 ? 150 : value1;
                break;
            case "BaseSkill_SeriesAttack1":
                skillRatio = value1 == 0 ? 30 : value1;
                break;
            case "BaseSkill_ReduceDefenceAttack1":
                skillRatio = 0;
                break;
            case "BuffSkill_BuffAtk1":
                skillRatio = value1 == 0 ? 5 : value1;
                break;
            case "BuffSkill_BuffAtk2":
                skillRatio = value1 == 0 ? 10 : value1;
                break;
        }

        skillRatio += skillAddRatioMap.getOrDefault(skillId,0d);

        damage.addDamage((baseDamage * skillRatio / 100d));

        //取得需要判断的目标

        int targetType = cardSkill.getTargetType();
        switch (targetType){
            default:
            case 0:
                break;
        }


        //对damage进行miss判定

        //对damage进行敌人防御&防御buff/debuff修正计算

        //对damage进行敌人属性修正计算
        //..........

        //对damage进行地形修正计算

        //对damage进行固定伤害加成
        damage.addDamage(fixDamage);

        //处理护盾抵消
        //.........

        long totalDamage = (long)damage.getTotalDamage();

        //扣除目标对象?
    }

    public boolean calcSkillMiss(){
        return false;
    }
}
