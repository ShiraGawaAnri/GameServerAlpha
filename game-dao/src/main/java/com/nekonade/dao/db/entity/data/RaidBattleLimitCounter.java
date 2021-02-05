package com.nekonade.dao.db.entity.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "RaidBattleLimitCounter")
public class RaidBattleLimitCounter {

    private long playerId;

    private String stageId;

    private int counter;

    private long timestamp;

}
