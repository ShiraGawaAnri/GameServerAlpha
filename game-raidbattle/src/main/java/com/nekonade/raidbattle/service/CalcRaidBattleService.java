package com.nekonade.raidbattle.service;


import com.nekonade.common.dto.EnumDTO;
import com.nekonade.common.dto.RaidBattleEffectDTO;
import com.nekonade.common.dto.RaidBattleEffectGroupDTO;
import com.nekonade.common.dto.RaidBattleTarget;
import com.nekonade.common.error.GameNotifyException;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.dao.daos.CardsDbDao;
import com.nekonade.common.enums.EnumEntityDB;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CalcRaidBattleService {


    @Getter
    @Setter
    private class Damage {

        private int miss = 0;

        private double totalDamage = 0L;


        public void addDamage(long damage) {
            this.totalDamage += damage;
        }

        public void addDamage(double damage) {
            this.totalDamage += damage;
        }

        public void addDamage(int damage) {
            this.totalDamage += damage;
        }
    }

    @Autowired
    private CardsDbDao cardsDbDao;

    public void calcCardAttack(RaidBattleManager dataManager, RaidBattle.Player actionPlayer, RaidBattleTarget actionSource, String cardId, int targetPos, List<Integer> selectCharaPos, long turn) {
        //使用DB来替代
        /*CardsDB cardsDB = cardsDbDao.findCardsDB(cardId);
        if(cardsDB == null){
            throw GameNotifyException.newBuilder(GameErrorCode.RaidBattleAttackInvalidParam).build();
        }*/
        CopyOnWriteArrayList<RaidBattle.Enemy> enemies = dataManager.getRaidBattle().getEnemies();

        Integer alive = actionSource.getAlive();

        int sourceType = actionSource.sourceType();

        boolean isPlayer = sourceType == EnumDTO.SourceType.RaidBattle_SourceType_Player.getType();

        boolean isEnemy = sourceType == EnumDTO.SourceType.RaidBattle_SourceType_Enemy.getType();

        Map<String, CardsDB> allCardsDB = cardsDbDao.findAllCardsDB();
        //随机使用卡片
        List<CardsDB> cardsDBS = new ArrayList<>(allCardsDB.values());
        if (cardsDBS.size() == 0) {
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
        ConcurrentHashMap<String, RaidBattleEffectDTO> debuffs = actionSource.getDebuffs();
        //特定debuff下无法行动
        if (debuffs != null && debuffs.size() > 0) {
            //.........
        }


        //不满足一定条件无法释放
        List<ActiveSkillsDB.Requires> requires = cardSkill.getRequires();

        if (requires != null && requires.size() > 0) {
            //.........
        }

        //是否必须装备什么
        Object equipment = cardSkill.getEquipment();
        if (equipment != null) {
            //.........
        }

        //是否要求处于某个buff/debuff下
        List<ActiveSkillsDB.Status> status = cardSkill.getStatus();
        if (status != null && status.size() > 0) {
            //........
        }

        //是否要求处于某个特殊场景
        int state = cardSkill.getState();
        if (state != EnumEntityDB.EnumNumber.RaidBattle_In_State_None.getValue()) {
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
        Integer atk = actionSource.getAtk();

        baseDamage = atk;

        damage.addDamage(baseDamage);

        //遍历角色身上装备,得出所有增加技能倍率的值并放进hashmap中
        Map<String, Double> skillAddRatioMap = new HashMap<>();
        //...........

        //计算技能倍率
        switch (skillId) {
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

        skillRatio = skillRatio * (1 + skillAddRatioMap.getOrDefault(skillId, 0d) / 100);



        //取得需要判断的目标
        int targetType = cardSkill.getTargetType();
        List<? extends RaidBattleTarget> livingTargets;
        if (isPlayer) {
            livingTargets = dataManager.getLivingEnemy(dataManager.getRaidBattle().getEnemies());
        } else {
            livingTargets = dataManager.getLivingCharacter(new ArrayList<>(actionPlayer.getParty().values()));
        }

        switch (targetType) {
            default:
            case 0:
            case 1://敌对目标
                targetPos = Math.min(livingTargets.size() - 1, targetPos);
                RaidBattleTarget target = livingTargets.get(targetPos);
                calcAttackMiss(actionSource, target, damage);
                if (damage.getMiss() == 1) {
                    break;
                }
                calcDefence(actionSource,target,damage);
                damage.setTotalDamage((damage.getTotalDamage() * skillRatio / 100d));

                damage.addDamage(fixDamage);

                long totalDamage = (long) damage.getTotalDamage();

                target.receivedDamage(totalDamage);

                break;
        }




        //对damage进行miss判定

        //对damage进行敌人防御&防御buff/debuff修正计算

        //对damage进行敌人属性修正计算
        //..........

        //对damage进行地形修正计算

        //对damage进行固定伤害加成


        //处理护盾抵消
        //.........



        //扣除目标对象?
    }

    public void calcAttackMiss(RaidBattleTarget source, RaidBattleTarget target, Damage damage) {

        damage.setMiss(0);
    }

    public void calcDefence(RaidBattleTarget source, RaidBattleTarget target, Damage damage) {

        ConcurrentHashMap<String, RaidBattleEffectDTO> buffs = target.getBuffs();

        ConcurrentHashMap<String, RaidBattleEffectDTO> debuffs = target.getDebuffs();

        List<String> buffDefNames = Stream.of("Buff_Def1", "Buff_Def2", "Buff_Def3").collect(Collectors.toList());

        List<String> debuffDefNames = Stream.of("Debuff_Def1", "Debuff_Def2", "Debuff_Def3", "Debuff_Def4").collect(Collectors.toList());

        Map<String, Double> tempBuffMap = new HashMap<>();
        Map<String, Double> tempDebuffMap = new HashMap<>();
        //EffectGroupId:该EffectGroup的合计值
        Map<String, Double> buffMap = buffs.values().stream()
                .filter(buff -> buffDefNames.contains(buff.getEffectId()))
                .reduce(tempBuffMap, (acc, item) -> {
                    String effectId = item.getEffectId();
                    int effectStack = item.getEffectStack();
                    int effectMaxStack = item.getEffectMaxStack();
                    double value = 0;
                    switch (effectId){
                        default:
                        case "Buff_Def1":
                        case "Buff_Def2":
                        case "Buff_Def3":
                            //有可能不是value1就是buff数值
                            //计算层数
                            if(effectMaxStack > 0){
                                value = item.getValue1() * (effectStack + 1);
                            }else{
                                value = item.getValue1();
                            }
                            break;
                    }

                    RaidBattleEffectGroupDTO effectGroup = item.getEffectGroup();
                    String effectGroupId = effectGroup.getEffectGroupId();
                    double groupMaxStackValue = effectGroup.getGroupMaxStackValue();
                    acc.computeIfPresent(effectGroupId, (k, v) -> v = 0d);
                    //是否允许该effectGroup的effect量叠加
                    if (effectGroup.getGroupOverlapping() == EnumEntityDB.EnumNumber.RaidBattle_EffectGroups_Overlapping.getValue()) {
                        double finalValue = value;
                        acc.computeIfPresent(effectGroupId, (k, v) -> {
                            v = Math.min(groupMaxStackValue,v + finalValue);
                            return v;
                        });
                    } else {
                        acc.computeIfPresent(effectGroupId, (k, v) -> {
                            v = Math.min(groupMaxStackValue,v);
                            return v;
                        });
                    }
                    return acc;
                }, (acc, item) -> null);
        Double buffDefValue = buffMap.values().stream().reduce(0d, (acc, item) -> {
            acc += item;
            return acc;
        });

        double tempDef = target.getDef() * (100 + buffDefValue) / 100;

        Map<String, Double> debuffMap = debuffs.values().stream()
                .filter(buff -> debuffDefNames.contains(buff.getEffectId()))
                .reduce(tempDebuffMap, (acc, item) -> {
                    String effectId = item.getEffectId();
                    int effectStack = item.getEffectStack();
                    int effectMaxStack = item.getEffectMaxStack();
                    double value = 0;
                    switch (effectId){
                        default:
                        case "Debuff_Def1":
                        case "Debuff_Def2":
                        case "Debuff_Def3":
                        case "Debuff_Def4":
                            //有可能不是value1就是buff数值
                            //计算层数
                            if(effectMaxStack > 0){
                                value = item.getValue1() * (effectStack + 1);
                            }else{
                                value = item.getValue1();
                            }
                            break;
                    }

                    RaidBattleEffectGroupDTO effectGroup = item.getEffectGroup();
                    String effectGroupId = effectGroup.getEffectGroupId();
                    double groupMaxStackValue = effectGroup.getGroupMaxStackValue();
                    acc.computeIfPresent(effectGroupId, (k, v) -> v = 0d);
                    //是否允许该effectGroup的effect量叠加
                    if (effectGroup.getGroupOverlapping() == EnumEntityDB.EnumNumber.RaidBattle_EffectGroups_Overlapping.getValue()) {
                        double finalValue = value;
                        acc.computeIfPresent(effectGroupId, (k, v) -> {
                            v = Math.min(groupMaxStackValue,v + finalValue);
                            return v;
                        });
                    } else {
                        acc.computeIfPresent(effectGroupId, (k, v) -> {
                            v = Math.min(groupMaxStackValue,v);
                            return v;
                        });
                    }
                    return acc;
                }, (acc, item) -> null);
        Double debuffDefValue = debuffMap.values().stream().reduce(0d, (acc, item) -> {
            acc += item;
            return acc;
        });

        tempDef = tempDef * (100 - debuffDefValue) / 100;

        //计算公式
        //....
        double baseAtk = damage.getTotalDamage();

        double v = (baseAtk * (4000 + tempDef) / (4000 + tempDef * 10));

        damage.setTotalDamage(v);

    }
}
