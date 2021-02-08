package com.nekonade.dao.db.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Card {

    @Id
    private long cardId;

    private String name;
}
