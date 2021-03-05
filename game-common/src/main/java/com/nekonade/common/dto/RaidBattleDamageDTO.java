package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class RaidBattleDamageDTO {

    private String raidId;

    //造成伤害的每一步仔细表现
    private List<? super _Command> scenario = new ArrayList<>();

    //状态
    private Status status = new Status();

    public <T extends _Command> void addScenario(T item) {
        scenario.add(item);
    }

    public abstract static class _Command {

    }

/*

    private Contribution contribution;

    private List<Condition> conditions;

    private List<UltimateTypeAttack> ultimateTypeAttacks;

    private List<MessageText> messageTexts;



*/

    @Getter
    @Setter
    public static class Condition extends _Command {

        private int pos;

        private String targetTo;

        private Map<String,RaidBattleEffectDTO> buffs;

        private Map<String,RaidBattleEffectDTO> debuffs;

    }

    @Getter
    @Setter
    public static class Damage extends _Command {

        private int element;

        private int targetTo;

        private long value;

        private long hp;

        private boolean critical;

        private int miss;

        private boolean guard;

        private String effect;

    }

    @Getter
    @Setter
    public static class Contribution extends _Command {

        private int amount;

        public void addAmount(int amount){
            this.amount += amount;
        }

    }

    @Getter
    @Setter
    public static class UltimateTypeAttack extends _Command {

        private String ultimateTypeAttackId;

        private int pos;

        private String targetTo;

        private int toPos;

        private int element;

        private List<Damage> damages;


    }

    @Getter
    @Setter
    public static class MessageText extends _Command {

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
    public static class Recast extends _Command {

        private int pos;

        private int type;

        private int value;

        private List<String> recasts;

    }

    @Getter
    @Setter
    public static class ModeChange extends _Command {

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
    public static class BossGauge extends _Command {

        private int pos;

        private int element;

        private long hp;

        private long maxHp;

    }

    @Getter
    @Setter
    public static class FormChange extends _Command {

        private int form;

        private int pos;

        private String targetTo;

        private Object param;
    }

    @Getter
    @Setter
    public static class Attack extends _Command {

        private String from;//来自 player/Enemy

        private int pos;//来自哪个位置的

        private List<Damage> damages = new ArrayList<>();

        private List<Additional> additionals = new ArrayList<>();

        private boolean concurrentAttack;

        private boolean allAttack;

        public void addDamage(Damage item) {
            damages.add(item);
        }
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