package com.nekonade.dao.db.entity.data;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("CardSkillsDB")
public class CardSkillsDB {

    @Id
    private String skillId;

    private int type;//0:normal 1.attack 2:support 3:heal 4:revive 5:special

    private String description;
}
