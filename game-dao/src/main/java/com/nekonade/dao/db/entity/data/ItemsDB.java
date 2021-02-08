package com.nekonade.dao.db.entity.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("ItemDB")
public class ItemsDB {

    @Indexed(unique = true, sparse = true)
    private String itemId;

    private String name;

    private int type;

    private int category;

    private long maxStack = 9999999;
}
