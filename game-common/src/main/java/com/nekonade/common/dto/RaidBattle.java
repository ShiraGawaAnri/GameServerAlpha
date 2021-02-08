package com.nekonade.common.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RaidBattle {

    private String raidId;

    private String stageId;

    private boolean multiRaid;

    private int area;

    private int episode;

    private int chapter;

    private int stage;

    private int difficulty;

    private int costStaminaPoint;

    private boolean costItem;

    private Map<String, Integer> costItemMap;

    private Object enemy;

    private boolean active = false;

    private boolean finish = false;

    private long limitCounter;

    private int limitCounterRefreshType;

    private long restTime = 1800 * 1000L;

    private long expired = -1;
}
