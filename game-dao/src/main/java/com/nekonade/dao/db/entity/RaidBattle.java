package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@Document(collection = "RaidBattle")
public class RaidBattle {

    private long ownerPlayerId;

    @Id
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

    private Map<String, Integer> costItemMap = new ConcurrentHashMap<>();

    //private List<com.nekonade.common.dto.Player> players;

    private CopyOnWriteArrayList<com.nekonade.common.dto.Player> players = new CopyOnWriteArrayList<>();

    private CopyOnWriteArrayList<Object> enemies = new CopyOnWriteArrayList<>();

    private boolean active = false;

    private boolean finish = false;

    private long limitCounter;

    private int limitCounterRefreshType;

    private long restTime = 1800 * 1000L;

    private long expired = -1;
}
