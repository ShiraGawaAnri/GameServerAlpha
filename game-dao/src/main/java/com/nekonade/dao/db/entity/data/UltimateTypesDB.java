package com.nekonade.dao.db.entity.data;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@Setter
@Document("UltimateTypesDB")
public class UltimateTypesDB implements Serializable {

    @Id
    private String typeId;

    @Indexed(unique = true,sparse = true)
    private int type;

    private String name;

    private String description;
}
