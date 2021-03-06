package com.nekonade.dao.db.entity.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nekonade.dao.db.entity.RaidBattleDirectiveEffect;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.beans.JavaBean;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document("CardsDB")
public class CardsDB {

    @Id
    private String cardId;

    private String name;

    private String description;

    private int cost = 1;

    private int load = 100;

    private int coolDownTurn = 0;

    private int coolDownTime = 0;

    private int maxLevel = 0;

    @DBRef
    private ActiveSkillsDB cardSkill;//卡牌只拥有1个Skill

    //用于动态修改Skill的参数
    private int value1;

    private int value2;

    private int value3;

    private int value4;


    private List<RaidBattleDirectiveEffect> effects;//卡牌可引起多种Buff/Debuff
}
