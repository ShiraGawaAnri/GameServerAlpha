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

        int defaultStarterValue = 20;

        int maxValue = 9999;

        int eachLevelAddPoint = 5;

        long recoverTime = 5 * 60 * 1000L;

        int recoverValue = 1;
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
