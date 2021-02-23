package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
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

    private CopyOnWriteArrayList<Map<String,Object>> buffs = new CopyOnWriteArrayList<>();

    private CopyOnWriteArrayList<Map<String,Object>> debuffs = new CopyOnWriteArrayList<>();
}
