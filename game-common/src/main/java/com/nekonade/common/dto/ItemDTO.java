package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDTO {

    private String itemId;

    private int amount;

    private int type;

    private int category;

    private long expired;

}
