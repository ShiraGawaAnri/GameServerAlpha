package com.nekonade.dao.db.entity.config;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

        int defaultStarterValue = 1;

        int maxValue = 150;

        int expRatio = 30;

        public long getNextLevelUpPoint(int level) {
            double v = expRatio * (Math.pow(level, 3.0) + 5 * level + 1) - 80;
            return (long) v;
        }

        private long getNextLevelUpNeedPoint(int level, Long exp) {
            return getNextLevelUpPoint(level) - exp;
        }
    }
}
