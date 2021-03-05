package com.nekonade.common.enums;

import lombok.Getter;

import java.util.List;


public class EnumEntityDB {

    @Getter
    public enum EnumString{

        RaidBattle_Effect_TargetTo_Character("chara"),
        RaidBattle_Effect_TargetTo_Enemy("enemy"),
        ;

        private final String value;


        EnumString(String value) {
            this.value = value;
        }
    }

    @Getter
    public enum EnumNumber{
        Week_Monday(1),
        Week_Tuesday(2),
        Week_Wednesday(3),
        Week_Thursday(4),
        Week_Friday(5),
        Week_Saturday(6),
        Week_Sunday(7),
        RaidBattle_Create_LimitCounterRefreshType_None(0),
        RaidBattle_Effect_Prop_Buff(0),
        RaidBattle_Effect_Prop_Debuff(1),
        RaidBattle_Effect_Prop_Field(2),
        RaidBattle_Effect_Type_Dot(1),
        RaidBattle_Effect_Type_Support(2),
        RaidBattle_Effect_Type_Heal(3),
        RaidBattle_Effect_Type_Revive(4),
        RaidBattle_Effect_Type_Special(5),
        RaidBattle_EffectGroups_MaxValue(0),
        RaidBattle_EffectGroups_Overlapping(1),
        RaidBattle_CardSkill_Type_Attack(1),
        RaidBattle_CardSkill_Type_Support(2),
        RaidBattle_CardSkill_Type_Heal(3),
        RaidBattle_CardSkill_Type_Revive(4),
        RaidBattle_CardSkill_Type_Special(5),
        RaidBattle_In_State_None(0),
        RaidBattle_In_State_Water(1),
        RaidBattle_In_State_Underground(2),
        RaidBattle_In_State_Air(3),
        RaidBattle_In_State_Space(4),
        ActiveSkill_Type_Near_Physical(0),
        ActiveSkill_Type_Long_Physical(1),
        ActiveSkill_Type_Magic(2),
        ActiveSkill_Type_Misc(3),
        ActiveSkill_TargetType_Enemy(1),//敌对单位
        ActiveSkill_TargetType_Place(2),//地面系
        ActiveSkill_TargetType_Self(3),//自身
        ActiveSkill_TargetType_Team(4),//队伍所有成员啊
        ActiveSkill_TargetType_Trap(5),//触发系 定时系
        ActiveSkill_TargetType_AllFriend(6),//所有友军
        Item_Type_Healing(0),
        Item_Type_Usable(1),
        Item_Type_Etc(2),
        Item_Type_Healing_Dialog(100),//通常只需在客户端实现
        Item_Type_Usable_Dialog(101),//通常只需在客户端实现
        Item_Category_Common(0),
        Item_Delay_Type_Unable(-1),//没有使用间隔
        Item_Delay_Type_Specific_Duration(0),//指定时间
        ;

        private final int value;

        EnumNumber(int value) {
            this.value = value;
        }

    }



}
