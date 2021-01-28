package com.nekonade.dao.db.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
public class Card {

    @Id
    private long cardId;

    private String name;
}
