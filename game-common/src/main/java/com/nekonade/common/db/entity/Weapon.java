package com.nekonade.common.db.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weapon {

    private String id;

    private boolean enable = true;

    private int type;

    private int category;

    private String equipedByHero;

    private boolean locked = false;

}
