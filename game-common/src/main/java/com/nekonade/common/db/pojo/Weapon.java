package com.nekonade.common.db.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weapon {

    protected String id;

    protected boolean enable = true;

    protected int type;

    protected int category;

    protected String equipedByHero;

    protected boolean locked = false;

}
