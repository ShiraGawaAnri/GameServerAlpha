package com.nekonade.raidbattle.event.user;

import com.nekonade.common.dto.PlayerDTO;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import lombok.Getter;

@Getter
public class JoinedRaidBattlePlayerInitCharacterEventUser extends BasicEventUser{

    private final PlayerDTO playerDTO;

    private final RaidBattleManager raidBattleManager;

    public JoinedRaidBattlePlayerInitCharacterEventUser(PlayerDTO playerDTO, RaidBattleManager raidBattleManager) {
        this.playerDTO = playerDTO;
        this.raidBattleManager = raidBattleManager;
    }
}
