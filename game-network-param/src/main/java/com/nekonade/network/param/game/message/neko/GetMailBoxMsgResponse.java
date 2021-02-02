package com.nekonade.network.param.game.message.neko;


import com.nekonade.common.db.pojo.Mail;
import com.nekonade.common.model.PageResult;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 206, messageType = EnumMesasageType.RESPONSE, serviceId = 101)
public class GetMailBoxMsgResponse extends AbstractJsonGameMessage<GetMailBoxMsgResponse.ResponseBody> {

    @Getter
    @Setter
    public static class ResponseBody {

        private PageResult<Mail> mail;

    }

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }
}
