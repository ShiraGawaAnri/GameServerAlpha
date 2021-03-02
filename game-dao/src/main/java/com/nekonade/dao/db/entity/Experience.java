package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Experience {

    Long exp;

    Long nextLevelExp;

    public void addExp(long exp){
        this.exp += exp;
    }
}
