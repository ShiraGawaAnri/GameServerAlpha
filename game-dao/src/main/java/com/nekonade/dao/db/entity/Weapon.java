package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weapon {

    protected String id;

    protected boolean enable = true;

    protected int type;

    protected int category;

    protected String equippedByHero;

    protected boolean locked = false;

}
