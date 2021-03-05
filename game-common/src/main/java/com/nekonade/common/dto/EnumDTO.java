package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

public class EnumDTO {


    @Getter
    public enum SourceType{
        RaidBattle_SourceType_Player(0,"chara"),
        RaidBattle_SourceType_Enemy(1,"enemy"),
        ;

        private final int type;

        private final String simpleName;

        SourceType(int type, String simpleName) {
            this.type = type;
            this.simpleName = simpleName;
        }



    }
}
