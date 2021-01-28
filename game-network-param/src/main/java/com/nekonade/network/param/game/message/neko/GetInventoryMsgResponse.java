package com.nekonade.network.param.game.message.neko;


import com.nekonade.common.db.entity.Inventory;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 203, messageType = EnumMesasageType.REQUEST, serviceId = 101)
public class GetInventoryMsgResponse extends AbstractJsonGameMessage<GetInventoryMsgResponse.RequestBody> {

    @Getter
    @Setter
    public static class RequestBody {
        private Inventory inventory;
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
