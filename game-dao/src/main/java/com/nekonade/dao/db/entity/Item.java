package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

    private String itemId;

    private Integer amount;

    private Integer type;

    private Integer category;

    private Long expired;

    private Long maxStack;
}
