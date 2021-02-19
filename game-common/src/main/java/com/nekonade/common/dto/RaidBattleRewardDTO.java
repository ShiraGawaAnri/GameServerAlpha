package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class RaidBattleRewardDTO {

    private long playerId;

    private String raidId;

    private List<ItemDTO> items;

    private Long timestamp;

    private Integer claimed;

}
