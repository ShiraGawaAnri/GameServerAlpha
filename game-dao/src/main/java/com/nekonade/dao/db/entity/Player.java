package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Document(collection = "Player")
@Getter
@Setter
public class Player {
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
    private LinkedBlockingQueue<String> tasks = new LinkedBlockingQueue<>();
    private Task task = new Task();
    //背包
    private Inventory inventory = new Inventory();

    @Override
    public String toString() {
        return "Player [playerId=" + playerId + ", nickName=" + nickName + ", level=" + level + ", lastLoginTime=" + lastLoginTime + "]";
    }


}
