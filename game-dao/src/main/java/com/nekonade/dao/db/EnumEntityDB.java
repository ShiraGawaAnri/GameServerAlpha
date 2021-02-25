package com.nekonade.dao.db;

import lombok.Getter;


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
        ;

        private final int value;

        EnumNumber(int value) {
            this.value = value;
        }
    }



}
