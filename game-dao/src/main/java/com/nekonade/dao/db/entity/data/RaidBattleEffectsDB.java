package com.nekonade.dao.db.entity.data;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.checkerframework.common.value.qual.IntRange;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document("RaidBattleEffectsDB")
public class RaidBattleEffectsDB {

    @Id
    private String effectId;

    private int effectProp;//0 : buff 1: debuff

    private int effectType;//0:undefined 1:attack 2:support 3:heal 4:defence 5:special

    @DBRef
    private RaidBattleEffectGroupsDB effectGroup;//表示同一组buff/debuff时,方便设计累计上限 0:common

    private int effectMaxStack = 0;//限制此effectId的最大叠层数 假设数值为5,则最大为1001_5 即第1+5层

    private int effectiveSecond = -1;//该buff/debuff有效时间 -1则代表使用其他判定

    private int effectiveTurn = -1;//该buff/debuff有效回合 可分别独立计算 -1则代表使用其他判定 两者皆为-1时表示永续

    private int dispelByOther = 1;//该buff/debuff允许被普通驱散技能驱散

    private int value1;//用于公式计算

    private int value2;

    private int value3;

    private int value4;

    private String description;
}
