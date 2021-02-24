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

    private String description;
}
