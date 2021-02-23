package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMessageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 208, messageType = EnumMessageType.RESPONSE, serviceId = 101)
public class GetRaidBattleRewardListMsgResponse extends AbstractJsonGameMessage<GetRaidBattleRewardListMsgResponse.PageResult> {

    @Override
    protected Class<PageResult> getBodyObjClass() {
        return PageResult.class;
    }

    @Getter
    @Setter
    public static class PageResult<T> extends com.nekonade.common.model.PageResult<T>{

    }
}
