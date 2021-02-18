package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@ToString
public class Hero implements Cloneable {

    @Indexed(unique = true,sparse = true)
    private String heroId;

    private ConcurrentHashMap<String, HeroSkill> skillMap;

    private int level;

    private String weaponId;

}
