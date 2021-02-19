package com.nekonade.dao.db.entity;

import com.nekonade.common.dto.EnemyDTO;
import com.nekonade.common.dto.HeroDTO;
import com.nekonade.common.dto.PlayerDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
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

    private CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();

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
    public static class Player extends PlayerDTO implements Cloneable{

        private long contributePoint;

        private int turn = 1;

        private int joinedTime;

        private boolean retreated = false;

        private CopyOnWriteArrayList<Map<String,Object>> buffs = new CopyOnWriteArrayList<>();

        private CopyOnWriteArrayList<Map<String,Object>> debuffs = new CopyOnWriteArrayList<>();

        @Override
        public Player clone() {
            Player target = new Player();
            //先进行简单的浅拷贝
            BeanUtils.copyProperties(this,target);
            {
                ConcurrentHashMap<String, HeroDTO> map = new ConcurrentHashMap<>();
                this.getHerosMap().forEach((k,v)->{
                    map.put(k,v.clone());
                });
                target.setHerosMap(map);
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
            CopyOnWriteArrayList<Player> list = new CopyOnWriteArrayList<>();
            this.getPlayers().forEach(player -> {
                list.add(player.clone());
            });
            target.setPlayers(list);
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
