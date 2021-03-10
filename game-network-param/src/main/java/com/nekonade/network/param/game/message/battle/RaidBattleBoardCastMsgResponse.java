package com.nekonade.network.param.game.message.battle;


import com.nekonade.common.dto.RaidBattleDTO;
import com.nekonade.common.gameMessage.AbstractJsonGameMessage;
import com.nekonade.common.gameMessage.EnumMessageGroup;
import com.nekonade.common.gameMessage.EnumMessageType;
import com.nekonade.common.gameMessage.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@GameMessageMetadata(messageId = 1010, messageType = EnumMessageType.RESPONSE, serviceId = 102,groupId = EnumMessageGroup.RAIDBATTLE)
public class RaidBattleBoardCastMsgResponse extends AbstractJsonGameMessage<RaidBattleBoardCastMsgResponse.ResponseBody> {

    @Override
    protected Class<RaidBattleBoardCastMsgResponse.ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    @Getter
    @Setter
    public static class ResponseBody {

        private long ownerPlayerId;

        private String raidId;

        private ConcurrentHashMap<Long, RaidBattleDTO.Player> players = new ConcurrentHashMap<>();

        private Integer maxPlayers = 30;

        private CopyOnWriteArrayList<RaidBattleDTO.Enemy> enemies = new CopyOnWriteArrayList<>();

        private Boolean finish = false;

        private Boolean failed = false;

        private Long expired = -1L;
    }
}



