package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

    private String itemId;

    private int count;

    private int type;

    private int category;

    private long expired;

}
