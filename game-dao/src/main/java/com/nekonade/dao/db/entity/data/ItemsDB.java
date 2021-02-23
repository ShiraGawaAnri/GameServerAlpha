package com.nekonade.dao.db.entity.data;

import com.mongodb.lang.NonNull;
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

    private Integer type = 0;

    private Integer category = 0;

    private Long maxStack = 9999999L;

    @Transient
    private Double prob;
}
