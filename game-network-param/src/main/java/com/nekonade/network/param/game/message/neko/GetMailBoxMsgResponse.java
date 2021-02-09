package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@GameMessageMetadata(messageId = 206, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class GetMailBoxMsgResponse extends AbstractJsonGameMessage<GetMailBoxMsgResponse.PageResult> {

    @Override
    protected Class<PageResult> getBodyObjClass() {
        return PageResult.class;
    }

    @Getter
    @Setter
    public static class PageResult<T> {

        private Integer pageNum;

        private Integer pageSize;

        private Long total;

        private Integer pages;

        private List<T> list;
    }
}
