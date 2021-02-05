package com.nekonade.common.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RaidBattle {

    private String stageId;

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

    private boolean active = false;

    private boolean finish = false;

    private long limitCounter;

    private String refreshType;

    private long restTime = 1800 * 1000L;
}
