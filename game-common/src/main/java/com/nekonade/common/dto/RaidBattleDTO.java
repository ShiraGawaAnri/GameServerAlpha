package com.nekonade.common.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class RaidBattleDTO {

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

    private Map<String, Integer> costItemMap = new HashMap<>();

    private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();

    private int maxPlayers = 30;

    private CopyOnWriteArrayList<Enemy> enemies = new CopyOnWriteArrayList<>();

    private boolean active = false;

    private boolean finish = false;

    private boolean failed = false;

    private long limitCounter;

    private int limitCounterRefreshType;

    private long restTime = 1800 * 1000L;

    private long expired = -1;

    @Getter
    @Setter
    public static class Player extends PlayerDTO {

        private long contributePoint;

        private int turn;

        private int joinedTime;

        private boolean retreated = false;
    }

    @Getter
    @Setter
    public static class Enemy extends EnemyDTO {

    }


}
