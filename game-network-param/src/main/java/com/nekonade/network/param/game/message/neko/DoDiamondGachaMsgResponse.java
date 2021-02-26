package com.nekonade.network.param.game.message.neko;


import com.nekonade.common.dto.CharacterDTO;
import com.nekonade.common.dto.RaidBattleDTO;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@GameMessageMetadata(messageId = 501, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class DoDiamondGachaMsgResponse extends AbstractJsonGameMessage<DoDiamondGachaMsgResponse.ResponseBody> {

    @Override
    protected Class<DoDiamondGachaMsgResponse.ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    @Getter
    @Setter
    public static class ResponseBody {

        private List<CharacterDTO> result;
    }
}
