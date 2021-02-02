package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@GameMessageMetadata(messageId = 206, messageType = EnumMesasageType.REQUEST, serviceId = 101)
public class GetMailBoxMsgRequest extends AbstractJsonGameMessage<GetMailBoxMsgRequest.RequestBody> {

    @Getter
    @Setter
    public static class RequestBody {

        private List<Integer> filter;

        private Integer page;

        private Integer limit;

        private Integer sort;

    }

    @Override
    protected Class<RequestBody> getBodyObjClass() {
        return RequestBody.class;
    }

}
