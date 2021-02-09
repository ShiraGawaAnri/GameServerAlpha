package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Document(collection = "Player")
@Getter
@Setter
public class Player implements Cloneable{

    @Id
    private long playerId;

    @Indexed(name = "nickName", unique = true, sparse = true)
    private String nickName;

    private int level = 1;

    private Stamina stamina = new Stamina();

    private Experience experience = new Experience();

    private long lastLoginTime;

    private long createTime = System.currentTimeMillis();
    //测试的时候使用的，正式情况下，要使用线程安全的ConcurrentHashMap
    private ConcurrentHashMap<String, String> heros = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Hero> herosMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
    private LinkedBlockingQueue<Task> tasks = new LinkedBlockingQueue<>();
    private Task task = new Task();
    private String zoneId;
    //背包
    private Inventory inventory = new Inventory();
    //疲劳值,耐久力

    @Override
    public String toString() {
        return "Player [playerId=" + playerId + ", nickName=" + nickName + ", level=" + level + ", lastLoginTime=" + lastLoginTime + "]";
    }

    @Override
    public Player clone() {
        Player obj = null;
        try {
            obj = (Player) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return new EqualsBuilder()
                .append(playerId, player.playerId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(playerId)
                .toHashCode();
    }
}
