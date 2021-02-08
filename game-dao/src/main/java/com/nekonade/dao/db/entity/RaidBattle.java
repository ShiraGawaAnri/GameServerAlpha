package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "RaidBattle")
public class RaidBattle {

    @Id
    private long playerId;

    private int challengeTimes;// 当前剩余的挑战次数


}
