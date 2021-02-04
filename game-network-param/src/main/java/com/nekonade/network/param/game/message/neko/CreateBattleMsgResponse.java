package com.nekonade.network.param.game.message.neko;


import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

@GameMessageMetadata(messageId = 401,messageType= EnumMesasageType.RESPONSE,serviceId = 101)
public class CreateBattleMsgResponse extends AbstractJsonGameMessage<CreateBattleMsgResponse.RaidBattle> {

    @Getter
    @Setter
    public static class RaidBattle{

        private String stageId;

        private String title;

        private String subTitle;

        private boolean multiRaid;

        private int area;

        private int episode;

        private int chapter;

        private int stage;

        private int difficulty;

        private int costStaminaPoint;

        private boolean costItem;

        private String costItemId;

        private int costItemCount;

        private Object enemy;

        private boolean finish = false;

    }


    @Override
    protected Class<CreateBattleMsgResponse.RaidBattle> getBodyObjClass() {
        return RaidBattle.class;
    }
}
