package com.nekonade.dao.db.entity.data;

import com.nekonade.dao.db.entity.config.RaidBattleDirectiveEffect;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @DBRef
    private CardSkillsDB cardSkill;

    private int value1;

    private int value2;

    private int value3;

    private int value4;

    private List<RaidBattleDirectiveEffect> effects = new ArrayList<>();
}
