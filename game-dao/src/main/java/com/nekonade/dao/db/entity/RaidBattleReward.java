package com.nekonade.dao.db.entity;


import com.nekonade.common.dto.ItemDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Document("RaidBattleReward")
@CompoundIndexes({
        @CompoundIndex(name = "playerId_idx", def = "{'playerId':1}"),
        @CompoundIndex(name = "raidId_idx", def = "{'raidId':1}"),
})
public class RaidBattleReward {

    private String raidId;

    private long playerId;

    private List<ItemDTO> items;

    private Long timestamp;

    private int claimed;
}
