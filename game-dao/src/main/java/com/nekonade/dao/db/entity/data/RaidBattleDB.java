package com.nekonade.dao.db.entity.data;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Document("RaidBattleDB")
@CompoundIndexes({
        @CompoundIndex(name = "area_idx", def = "{'area':1}"),
        @CompoundIndex(name = "episode_idx", def = "{'episode':1}"),
        @CompoundIndex(name = "chapter_idx", def = "{'chapter':1}"),
        @CompoundIndex(name = "stage_idx", def = "{'stage':1}"),
        @CompoundIndex(name = "difficulty", def = "{'stage':1}"),
})
public class RaidBattleDB {

    @Indexed(unique = true, sparse = true)
    private String stageId;

    private boolean multiRaid = false;

    private int area = 1;

    private int episode = 1;

    private int chapter = 1;

    private int stage = 1;

    private int difficulty = 1;

    private int costStaminaPoint = 1;

    private boolean costItem = false;

    private Map<String, Integer> costItemMap = new ConcurrentHashMap<>();

    private int maxPlayers = 30;

    private boolean active = true;

    private long limitCounter = 0;

    private int limitCounterRefreshType = 0;

    private long limitTime = 1800 * 1000L;
}
