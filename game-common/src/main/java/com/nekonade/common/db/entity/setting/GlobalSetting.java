package com.nekonade.common.db.entity.setting;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GlobalSetting {

    private Stamina stamina = new Stamina();

    @Getter
    @Setter
    public static class Stamina{
        Integer defaultStarterValue = 20;

        Integer eachLevelAddPoint = 5;

        Long recoverTime = 5 * 60 * 1000L;
    }
}
