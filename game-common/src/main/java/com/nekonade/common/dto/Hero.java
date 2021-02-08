package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Hero {

    private String heroId;

    private ConcurrentHashMap<String, HeroSkill> skillMap;

    private int level;

    private String weaponId;
}
