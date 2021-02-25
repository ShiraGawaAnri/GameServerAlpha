package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class EnemyDTO {

    private String monsterId;

    private String name;

    private Integer key = 1;

    private Integer maxHp = 100;

    private Integer target = 0;

    private volatile Integer hp = getMaxHp();

    private volatile Integer alive = 1;

    private ConcurrentHashMap<String,Object> buffs = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String,Object> debuffs = new ConcurrentHashMap<>();
}
