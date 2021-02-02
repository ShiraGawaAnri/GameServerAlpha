package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Weapon {

    private String weaponId;

    private boolean enable = true;

    private int type;

    private int category;

    private String equippedByHero;

    private boolean locked = false;

    private String uniqueId;

}
