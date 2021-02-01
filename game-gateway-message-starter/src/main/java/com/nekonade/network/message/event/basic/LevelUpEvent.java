package com.nekonade.network.message.event.basic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LevelUpEvent extends UserBasicEvent{

    private int beforeLevel;

    private int afterLevel;

    private long nextLevelExperience;

    private int beforeStamina;

    private int afterStamina;

    private int nowStamina;
}
