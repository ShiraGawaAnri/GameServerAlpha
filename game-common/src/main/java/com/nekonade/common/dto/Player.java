package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.concurrent.ConcurrentHashMap;


@Getter
@Setter
public class Player {

    private long playerId;

    private String nickName;

    private int level = 1;

    private ConcurrentHashMap<String, Hero> herosMap = new ConcurrentHashMap<>();
}
