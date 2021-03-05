package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class RaidBattleDamageDTO {

    //造成伤害的每一步仔细表现
    private List<? extends command> scenario;

    //状态
    private Status status;

    @Getter
    @Setter
    public static class command{

    }

/*

    private Contribution contribution;

    private List<Condition> conditions;

    private List<UltimateTypeAttack> ultimateTypeAttacks;

    private List<MessageText> messageTexts;



*/

    @Getter
    @Setter
    public static class Condition extends command{

        private int pos;

        private String targetTo;

        private Map<String,RaidBattleEffectDTO> buffs;

        private Map<String,RaidBattleEffectDTO> debuffs;

    }

    @Getter
    @Setter
    public static class Damage extends command{

        private int element;

        private int pos;

        private long value;

        private long hp;

        private boolean critical;

        private int miss;

        private boolean guard;

        private String effect;
    }

    @Getter
    @Setter
    public static class Contribution extends command{

        private int amount;

    }

    @Getter
    @Setter
    public static class UltimateTypeAttack extends command{

        private String ultimateTypeAttackId;

        private int pos;

        private String targetTo;

        private int toPos;

        private int element;

        private List<Damage> damage;


    }

    @Getter
    @Setter
    public static class MessageText extends command{

        private Data data;

        @Getter
        @Setter
        public static class Data {

            private List<MessageTarget> characters;

            private List<MessageTarget> bosses;

            @Getter
            @Setter
            public static class MessageTarget {

                private int pos;

                private int type;

                private String text;

                private String status;

                private int miss;

                private Additional additional;

            }


        }
    }

    @Getter
    @Setter
    public static class Recast extends command{

        private int pos;

        private int type;

        private int value;

        private List<String> recasts;

    }

    @Getter
    @Setter
    public static class ModeChange extends command{

        private int pos;

        private int mode;

        private double gauge;

        private Additional additional;

    }

    @Getter
    @Setter
    public static class Additional{

        private MessageText message;

        private String effect;
    }

    @Getter
    @Setter
    public static class BossGauge extends command{

        private int pos;

        private int element;

        private long hp;

        private long maxHp;

    }

    @Getter
    @Setter
    public static class FormChange extends command{

        private int form;

        private int pos;

        private String targetTo;

        private Object param;
    }

    @Getter
    @Setter
    public static class Attack extends command{

        private String from;

        private int pos;

        private List<Damage> damages;

        private List<Additional> additionals;

        private boolean concurrentAttack;

        private boolean allAttack;
    }

    @Getter
    @Setter
    public static class Cards {

        private int pos;

        private List<CardDTO> list;

    }


    @Getter
    @Setter
    public static class Status{

        private List<ModeChange> bossModes;

        private List<Guard> guards;

        private double chainBurstGauge;

        private List<Cards> cards;

        @Getter
        @Setter
        public static class Guard {

            private int pos;

            private boolean guarding;

            private boolean unavailable;

        }


    }
}
