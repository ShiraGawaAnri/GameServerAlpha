package com.nekonade.common.db.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

    private String id;

    private int count;

    private int type;

    private int category;

    private Long expired;

}