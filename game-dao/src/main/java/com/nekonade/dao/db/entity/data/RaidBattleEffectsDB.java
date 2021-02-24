package com.nekonade.dao.db.entity.data;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document("RaidBattleEffectsDB")
public class RaidBattleEffectsDB {

    private int effectProp;//0 : buff 1: debuff

    @NonNull
    @Indexed(unique = true)
    private String effectId;

    private int effectType;//0:undefined 1:attack 2:support 3:heal 4:defence 5:special

    private List<String> effectGroupId;//表示同一组buff/debuff时,方便设计累计上限 0:common

    private String effectTeamId;//用以设计叠层buff,如effectId:1001,叠加2层则返回的buffId变成effectId:1000_2

    private int effectTeamMaxLength = 5;//限制该Team的最大叠层数 如1001_5 即第6层

    private int effectiveSecond;//该buff/debuff有效时间 -1则代表无限

    private int effectiveTurn;//该buff/debuff有效回合 可分别独立计算 -1则代表无限

    private int dispelByOther;//该buff/debuff允许被普通驱散技能驱散
}
