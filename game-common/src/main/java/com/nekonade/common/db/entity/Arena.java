package com.nekonade.common.db.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Arena")
public class Arena {
    @Id
    private long playerId;
    private int challengeTimes;// 当前剩余的挑战次数

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getChallengeTimes() {
        return challengeTimes;
    }

    public void setChallengeTimes(int challengeTimes) {
        this.challengeTimes = challengeTimes;
    }


}
