package com.nekonade.dao.db.entity.data;


import com.nekonade.dao.db.entity.RaidBattle;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@Document("RaidBattleDB")
@CompoundIndexes({
        @CompoundIndex(name = "episode_idx", def = "{'episode':1}"),
        @CompoundIndex(name = "chapter_idx", def = "{'chapter':1}"),
        @CompoundIndex(name = "stage_idx", def = "{'stage':1}"),
        @CompoundIndex(name = "difficulty_idx", def = "{'difficulty':1}"),
        @CompoundIndex(name = "area_idx", def = "{'area':1}"),
})
public class RaidBattleDB {

    @Indexed(unique = true, sparse = true)
    private String stageId;

    private boolean multiRaid = false;

    private Integer area = 1;

    private Integer episode = 1;

    private Integer chapter = 1;

    private Integer stage = 1;

    private Integer difficulty = 1;

    private Integer costStaminaPoint = 1;

    private Map<String, Integer> costItemMap = new HashMap<>();

    @DBRef
    private List<EnemiesDB> enemyList = new ArrayList<>();

    private List<String> enemyIds = new ArrayList<>();

    @DBRef
    private RewardsDB reward = new RewardsDB();

    private Integer maxPlayers = 30;

    private Boolean active = true;

    private Long limitCounter = 0L;

    private Integer limitCounterRefreshType = 0;

    private Long limitTime = 1800 * 1000L;
}
