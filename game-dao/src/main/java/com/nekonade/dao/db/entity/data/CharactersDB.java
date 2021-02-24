package com.nekonade.dao.db.entity.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("CharactersDB")
public class CharactersDB {

    @Id
    private String charaId;

    private String name;

    private int baseHp = 100;

    private int baseCost = 10;

    private int baseSpeed = 100;

    private EnergyStrikeTypesDB energyStrikeTypes;

    private int baseGuard = 100;


}
