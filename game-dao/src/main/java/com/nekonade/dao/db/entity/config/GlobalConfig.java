package com.nekonade.dao.db.entity.config;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@Document(collection = "GlobalConfig")
public class GlobalConfig {

    @Id
    private long id = 1;

    private long version = 1001;

    private Stamina stamina = new Stamina();

    private Level level = new Level();

    private Diamond diamond = new Diamond();

    private Character character = new Character();

    @Getter
    @Setter
    public static class Stamina {

        private int defaultStarterValue = 20;

        private int maxValue = 9999;

        private double staminaFactor = 0.3;

        private int eachLevelAddPoint = 25;

        private long recoverTime = 5 * 60 * 1000L;

        private int recoverValue = 1;

        public int CalcStaminaMaxValue(int playerLevel){
            return this.defaultStarterValue + this.eachLevelAddPoint * (playerLevel - 1);
        }
    }

    @Getter
    @Setter
    public static class Level {

        private int defaultStarterValue = 1;

        private int maxValue = 150;

        private int expRatio = 30;

        public long getNextLevelUpPoint(int level) {
            double v = expRatio * (Math.pow(level, 3.0) + 5 * level + 1) - 80;
            return (long) v;
        }

        private long getNextLevelUpNeedPoint(int level, Long exp) {
            return getNextLevelUpPoint(level) - exp;
        }
    }

    @Getter
    @Setter
    public static class Diamond {

        private long maxValue = 9999999999L;
    }

    @Getter
    @Setter
    public static class Character{

        private Map<String,StatusDataBase> statusDataBase = new HashMap<>();//素质方面设定


        @Getter
        @Setter
        public static class StatusDataBase {

            private String charaId;

            private double hpFactor = 1.0;

            private double hpMultiplicator = 1.0;

            private double atkFactor = 1.0;

            private double defFactor = 1.0;

            private double speedFactor = 1.0;
        }
    }
}
