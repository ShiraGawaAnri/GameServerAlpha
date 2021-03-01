package com.nekonade.dao.db.entity;


import com.nekonade.dao.db.EnumEntityDB;
import com.nekonade.dao.db.entity.data.RaidBattleEffectsDB;
import lombok.Getter;
import lombok.Setter;


/**
 * 描述由Card引起的Buff和Debuff
 *
 */
@Getter
@Setter
public class RaidBattleDirectiveEffect {

    private String targetTo = EnumEntityDB.EnumString.RaidBattle_Effect_TargetTo_Player.getValue();//player or enemy

    private int posType;//适用的位置类型 0:自身 1:指定对象 2:指定范围 3:指定对象及其范围

    private int pos;//位置下标

    private int range;//当posType = 2,3时有效

    private double rate = 100.0d;//命中/触发率

    private boolean dependSkillHit = true;//是否依赖技能命中才能给予

    private RaidBattleEffectsDB effect;//效果
}