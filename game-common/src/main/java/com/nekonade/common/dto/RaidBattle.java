package com.nekonade.common.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class RaidBattle {

    private long ownerPlayerId;

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

    private ConcurrentHashMap<String, Integer> costItemMap = new ConcurrentHashMap<>();

    private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();

    private CopyOnWriteArrayList<Object> enemies = new CopyOnWriteArrayList<>();

    private boolean active = false;

    private boolean finish = false;

    private long limitCounter;

    private int limitCounterRefreshType;

    private long restTime = 1800 * 1000L;

    private long expired = -1;
}
