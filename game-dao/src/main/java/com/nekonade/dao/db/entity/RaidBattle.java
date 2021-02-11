package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
    @Indexed(unique = true,sparse = true)
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

    private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();
    private int maxPlayers = 30;

    private CopyOnWriteArrayList<Object> enemies = new CopyOnWriteArrayList<>();

    private boolean active = false;

    private boolean finish = false;

    private boolean failed = false;

    private long limitCounter;

    private int limitCounterRefreshType;

    private long restTime = 1800 * 1000L;

    private long expired = -1;

    @Getter
    @Setter
    public static class Player extends com.nekonade.common.dto.Player{

        private long contributePoint;

        private int turn;

        private int joinedTime;

        private boolean retreated = false;
    }
}
