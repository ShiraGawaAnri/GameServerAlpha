package com.nekonade.network.param.game.message.neko;


import com.nekonade.common.db.pojo.Inventory;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 203, messageType = EnumMesasageType.RESPONSE, serviceId = 101)
public class GetInventoryMsgResponse extends AbstractJsonGameMessage<GetInventoryMsgResponse.RequestBody> {

    @Getter
    @Setter
    public static class RequestBody {
        private Object inventory;
    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }
}
