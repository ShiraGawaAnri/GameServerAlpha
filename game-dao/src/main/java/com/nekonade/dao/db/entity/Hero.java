package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@ToString
public class Hero implements Cloneable{

    private String heroId;

    private ConcurrentHashMap<String ,HeroSkill> skillMap;

    private int level;

    private String weaponId;
    
}
