package com.mygame.game.message;

import com.mygame.game.common.AbstractJsonGameMessage;
import com.mygame.game.message.SecondMsgResponse.SecondMsgResponseBody;
import com.xinyue.network.message.game.EnumMesasageType;
import com.xinyue.network.message.game.GameMessageMetadata;

@GameMessageMetadata(messageId = 10002, messageType = EnumMesasageType.RESPONSE, serviceId = 1)
public class SecondMsgResponse extends AbstractJsonGameMessage<SecondMsgResponseBody> {

    @Override
    protected Class<SecondMsgResponseBody> getBodyObjClass() {
        return SecondMsgResponseBody.class;
    }

    public static class SecondMsgResponseBody {
        private long result1;
        private String result2;

        public long getResult1() {
            return result1;
        }

        public void setResult1(long result1) {
            this.result1 = result1;
        }

        public String getResult2() {
            return result2;
        }

        public void setResult2(String result2) {
            this.result2 = result2;
        }
    }
}


