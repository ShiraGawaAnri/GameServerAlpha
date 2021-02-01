package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

    private long itemId;

    private int count;

    private int type;

    private int category;

    private long expired;

}
