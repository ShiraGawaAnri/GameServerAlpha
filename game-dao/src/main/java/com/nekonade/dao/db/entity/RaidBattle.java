package com.nekonade.dao.db.entity;

import com.nekonade.common.dto.EnemyDTO;
import com.nekonade.common.dto.CharacterDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@Document(collection = "RaidBattle")
public class RaidBattle implements Cloneable{

    private Long ownerPlayerId;

    @Id
    private String raidId;

    private String stageId;

    private Boolean multiRaid;

    private Integer area;

    private Integer episode;

    private Integer chapter;

    private Integer stage;

    private Integer difficulty;

    private Integer costStaminaPoint;

    private Map<String, Integer> costItemMap = new HashMap<>();

    private ConcurrentHashMap<Long,Player> players = new ConcurrentHashMap<>();

    private Integer maxPlayers = 30;

    private CopyOnWriteArrayList<Enemy> enemies = new CopyOnWriteArrayList<>();

    private Boolean active = false;

    private volatile boolean finish = false;

    private volatile boolean failed = false;

    private Long limitCounter;

    private Integer limitCounterRefreshType;

    private Long restTime = 1800 * 1000L;

    private Long expired = -1L;

    @Getter
    @Setter
    public static class Player implements Cloneable{

        private long playerId;

        private String nickName;

        private Integer level = 1;

        private ConcurrentHashMap<String, Character> party = new ConcurrentHashMap<>();

        private long contributePoint;

        private int turn = 1;

        private int joinedTime;

        private boolean retreated = false;

        private ConcurrentHashMap<String,Object> buffs = new ConcurrentHashMap<>();

        private ConcurrentHashMap<String,Object> debuffs = new ConcurrentHashMap<>();

        @Getter
        @Setter
        public static class Character extends CharacterDTO implements Cloneable{

            private Integer maxHp = 1;

            private Double maxSpeed = 1d;

            private Integer maxGuard = 1;

            private Integer maxCost = 1;

            private Integer maxAtk = 1;

            private Integer maxDef = 1;

            private List<Object> cardsDeck = new CopyOnWriteArrayList<>();

            @Override
            public Character clone() {
                Character target = new Character();
                BeanUtils.copyProperties(this,target);
                return target;
            }
        }


        @Override
        public Player clone() {
            Player target = new Player();
            //先进行简单的浅拷贝
            BeanUtils.copyProperties(this,target);
            {
                ConcurrentHashMap<String, Character> map = new ConcurrentHashMap<>();
                this.getParty().forEach((k, v)->{
                    map.put(k,v.clone());
                });
                target.setParty(map);
            }
            return target;
        }
    }

    @Getter
    @Setter
    public static class Enemy extends EnemyDTO implements Cloneable{
        @Override
        public Enemy clone() {
            Enemy target = new Enemy();
            //先进行简单的浅拷贝
            BeanUtils.copyProperties(this,target);
            {
//                ConcurrentHashMap<String, Hero> map = new ConcurrentHashMap<>();
//                this.getHerosMap().forEach((k,v)->{
//                    map.put(k,v.clone());
//                });
//                target.setHerosMap(map);
            }
            return target;
        }
    }




    @Override
    public RaidBattle clone() {
        RaidBattle target = new RaidBattle();
        //先进行简单的浅拷贝
        BeanUtils.copyProperties(this,target);
        {
            ConcurrentHashMap<Long,Player> map = new ConcurrentHashMap<>();
            this.getPlayers().forEach((playerId,player) -> {
                map.put(playerId,player.clone());
            });
            target.setPlayers(map);
        }
        {
            CopyOnWriteArrayList<Enemy> list = new CopyOnWriteArrayList<>();
            this.getEnemies().forEach(enemy -> {
                list.add(enemy.clone());
            });
            target.setEnemies(list);
        }

        return target;
    }


}
