package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 210, messageType = EnumMesasageType.RESPONSE, serviceId = 102)
public class BuyArenaChallengeTimesMsgResponse extends AbstractJsonGameMessage<BuyArenaChallengeTimesMsgResponse.ResponseBody> {

    @Override
    protected Class<ResponseBody> getBodyObjClass() {
        return ResponseBody.class;
    }

    public static class ResponseBody {
        private int times;

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

    }
}
