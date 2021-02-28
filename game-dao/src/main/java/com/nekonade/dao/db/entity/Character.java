package com.nekonade.dao.db.entity;

import com.nekonade.dao.db.entity.data.UltimateTypesDB;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@ToString
public class Character implements Cloneable {

    @Indexed(unique = true,sparse = true)
    private String charaId;

    private Integer level = 1;
}
