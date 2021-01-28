package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Document(collection = "Player")
@Getter
@Setter
public class Player {


    public Player() {
        super();
    }

    @Id
    private long playerId;
    private String nickName;
    private int level;
    private long lastLoginTime;
    private long createTime;
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
    private Stamina stamina = new Stamina();

    @Override
    public String toString() {
        return "Player [playerId=" + playerId + ", nickName=" + nickName + ", level=" + level + ", lastLoginTime=" + lastLoginTime + "]";
    }


}
