package com.nekonade.network.param.game.message.neko;


import com.nekonade.common.dto.CharacterDTO;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@GameMessageMetadata(messageId = 209, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class GetPlayerCharacterListMsgResponse extends AbstractJsonGameMessage<GetPlayerCharacterListMsgResponse.RequestBody> {

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

    @Getter
    @Setter
    public static class RequestBody {

        private Map<String, CharacterDTO> characterMap = new HashMap<>();
    }

}
