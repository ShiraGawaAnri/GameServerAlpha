package com.nekonade.network.message.manager;


import com.nekonade.dao.db.entity.RaidBattle;

public class RaidBattleManager {

    private final RaidBattle raidBattle;

    public RaidBattleManager(RaidBattle raidBattle) {
        this.raidBattle = raidBattle;
    }

    public RaidBattle getRaidBattle() {
        return raidBattle;
    }

    public void addChallengeTimes(int times) {
        int result = raidBattle.getChallengeTimes() + times;
        raidBattle.setChallengeTimes(result);
    }
}
