package com.nekonade.dao.db.entity.data;

import com.nekonade.dao.seq.AutoIncKey;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("ItemDB")
public class ItemsDB {

    @Id
    @AutoIncKey
    private long id;

    @Indexed(unique = true,sparse = true)
    private String itemId;

    private String name;

    private int type;

    private int category;
}
