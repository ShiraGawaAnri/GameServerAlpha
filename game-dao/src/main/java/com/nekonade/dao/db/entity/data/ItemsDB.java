package com.nekonade.dao.db.entity.data;

import com.mongodb.lang.NonNull;
import com.nekonade.common.draw.DrawProb;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("ItemsDB")
public class ItemsDB {

    @Indexed(unique = true, sparse = true)
    private String itemId;

    @NonNull
    private String name;

    private int type;

    private int category;

    private long maxStack = 9999999;

    @Transient
    private double prob;
}
