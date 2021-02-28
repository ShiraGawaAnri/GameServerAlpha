package com.nekonade.dao.db;

import lombok.Getter;

import java.util.List;


public class EnumEntityDB {

    @Getter
    public enum EnumString{

        RaidBattle_Effect_TargetTo_Player("player"),
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
        Card_Type_Near_Physical(0),
        Card_Type_Long_Physical(1),
        Card_Type_Magic(2),
        Card_Type_Misc(3),
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
