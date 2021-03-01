package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

public class EnumDTO {


    @Getter
    public enum SourceType{
        RaidBattle_SourceType_Player(0),
        RaidBattle_SourceType_Enemy(1),
        ;

        private final int type;


        SourceType(int type) {
            this.type = type;
        }
    }
}
