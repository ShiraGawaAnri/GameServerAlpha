package com.nekonade.raidbattle.event.user;

import com.nekonade.common.dto.RaidBattleDamageDTO;
import com.nekonade.common.gameMessage.IGameMessage;
import com.nekonade.raidbattle.manager.RaidBattleManager;
import com.nekonade.raidbattle.message.context.RaidBattleMessageContext;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PushRaidBattleDamageDTOEventUser extends BasicEventUser {

    private final IGameMessage request;

    private final RaidBattleDamageDTO damageDTO;

    public PushRaidBattleDamageDTOEventUser(IGameMessage request, RaidBattleDamageDTO damageDTO) {
        this.request = request;
        this.damageDTO = damageDTO;
    }
}
