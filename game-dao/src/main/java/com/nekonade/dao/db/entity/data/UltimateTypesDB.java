package com.nekonade.dao.db.entity.data;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("UltimateTypesDB")
public class UltimateTypesDB {

    @Id
    private String typeId;

    private int type;

    private String name;

    private String description;
}