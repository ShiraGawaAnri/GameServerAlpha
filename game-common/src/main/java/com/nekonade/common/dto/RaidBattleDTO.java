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

    private Boolean multiRaid;

    private Integer area;

    private Integer episode;

    private Integer chapter;

    private Integer stage;

    private Integer difficulty;

    private Integer costStaminaPoint;

    private Map<String, Integer> costItemMap = new HashMap<>();

    private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();

    private Integer maxPlayers = 30;

    private CopyOnWriteArrayList<Enemy> enemies = new CopyOnWriteArrayList<>();

    private Boolean active = false;

    private Boolean finish = false;

    private Boolean failed = false;

    private Long limitCounter;

    private Integer limitCounterRefreshType;

    private Long restTime = 1800 * 1000L;

    private Long expired = -1L;

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
