package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class EnemyDTO extends RaidBattleTarget{

    private String monsterId;

    private String name;

    private Integer key = 1;

    private Integer target = 0;

    @Override
    public int sourceType() {
        return EnumDTO.SourceType.RaidBattle_SourceType_Enemy.getType();
    }
}
