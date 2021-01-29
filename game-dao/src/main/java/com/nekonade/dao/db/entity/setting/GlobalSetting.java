package com.nekonade.dao.db.entity.setting;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document
public class GlobalSetting {

    private Stamina stamina = new Stamina();

    @Getter
    @Setter
    public static class Stamina{

        int defaultStarterValue = 20;

        int eachLevelAddPoint = 5;

        Long recoverTime = 5 * 60 * 1000L;

        int recoverValue = 1;
    }
}
