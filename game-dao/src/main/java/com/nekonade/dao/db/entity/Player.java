package com.nekonade.dao.db.entity;

import com.mongodb.lang.NonNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
@Setter
@Document(collection = "Player")
public class Player{

    @Id
    private long playerId;

    @NonNull
    private String nickName;

    private Integer level = 1;

    private Stamina stamina = new Stamina();

    private Experience experience = new Experience();

    private Long lastLoginTime;

    private Long createTime = System.currentTimeMillis();
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
