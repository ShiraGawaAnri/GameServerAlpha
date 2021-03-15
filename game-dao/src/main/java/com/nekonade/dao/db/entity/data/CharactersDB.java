package com.nekonade.dao.db.entity.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@Document("CharactersDB")
public class CharactersDB implements Serializable {

    @Id
    private String charaId;

    private String name;

    private Integer baseHp = 100;

    private Integer baseCost = 10;

    private Double baseSpeed = 100d;

    private Integer baseGuard = 100;

    private UltimateTypesDB ultimateType;


}
