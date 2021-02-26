package com.nekonade.dao.db.entity;

import com.nekonade.dao.db.entity.data.UltimateTypesDB;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@ToString
public class Character implements Cloneable {

    @Indexed(unique = true,sparse = true)
    private String charaId;

    private Integer level = 1;
/*
    private String name;

    private ConcurrentHashMap<String, CharacterSkill> skillMap;


    private Integer hp = 1;

    private Integer maxHp = 1;

    private Integer maxSpeed = 1;

    private Integer maxGuard = 1;

    private Integer cost = 1;

    private Integer atk = 1;

    private Integer def = 1;

    private UltimateTypesDB ultimateType;

*/
}
