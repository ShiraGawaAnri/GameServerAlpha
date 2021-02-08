package com.nekonade.network.param.game.message.neko;


import com.nekonade.common.dto.Item;
import com.nekonade.common.dto.Weapon;
import com.nekonade.network.param.game.common.AbstractJsonGameMessage;
import com.nekonade.network.param.game.common.EnumMesasageType;
import com.nekonade.network.param.game.common.GameMessageMetadata;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

@GameMessageMetadata(messageId = 203, messageType = EnumMesasageType.RESPONSE, serviceId = 101)
public class GetInventoryMsgResponse extends AbstractJsonGameMessage<GetInventoryMsgResponse.Inventory> {

    @Override
    protected Class<Inventory> getBodyObjClass() {
        return Inventory.class;
    }

    @Getter
    @Setter
    public static class Inventory {
        //武器包
        private ConcurrentHashMap<String, Weapon> weaponMap;
        //道具包
        private ConcurrentHashMap<String, Item> itemMap;
    }
}
