package com.nekonade.dao.db.entity.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;


@Getter
@Setter
public class PlayerBasic {
    @Id
    private long playerId;
    private String nickName;
    private String headImg;
    private int level;
    private long lastLoginTime;
    private long createTime;
    private long userId;
    private int gold;
    private int energy;
    private int exp;
    private int score;//积分
    private int bullet;//子弹数量

}
