package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RaidBattleRewardDTO {

    private long playerId;

    private String raidId;

    private List<ItemDTO> items = new ArrayList<>();

    private Long timestamp;

    private Integer claimed;

}
