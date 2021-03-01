package com.nekonade.common.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public abstract class RaidBattleTarget {

    private String gid;

    private Integer level = 1;

    private volatile Long hp = 1L;

    private volatile Long maxHp = 1L;

    private volatile Double speed = 1d;

    private volatile Integer guard = 1;

    private volatile Integer atk = 1;

    private volatile Integer def = 1;

    private volatile Integer alive = 1;

    private volatile Double maxSpeed = 1d;

    private volatile Integer maxGuard = 1;

    private volatile Integer maxAtk = 1;

    private volatile Integer maxDef = 1;

    private UltimateTypes ultimateType;

    private ConcurrentHashMap<String,RaidBattleEffectDTO> buffs = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String,RaidBattleEffectDTO> debuffs = new ConcurrentHashMap<>();

    public static class UltimateTypes extends UltimateTypesDTO{

    }

    public synchronized void receivedDamage(long damage){
        this.hp = Math.max(0,this.hp - damage);
    }

    public abstract int sourceType();
}
