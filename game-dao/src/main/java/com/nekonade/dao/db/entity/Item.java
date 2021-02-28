package com.nekonade.dao.db.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

    private String itemId;

    private Integer amount;

    private Long expired;

    private String uniqueId;

    private Long delay;
}
