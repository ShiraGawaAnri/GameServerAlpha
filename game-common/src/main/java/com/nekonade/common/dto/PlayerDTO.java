package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.concurrent.ConcurrentHashMap;


@Getter
@Setter
public class PlayerDTO implements Cloneable{

    @Indexed
    private long playerId;

    private String nickName;

    private Integer level = 1;

    private ConcurrentHashMap<String, CharacterDTO> characters = new ConcurrentHashMap<>();

    @Override
    public PlayerDTO clone() {
        PlayerDTO target = new PlayerDTO();
        //先进行简单的浅拷贝
        BeanUtils.copyProperties(this,target);
        {
            ConcurrentHashMap<String, CharacterDTO> map = new ConcurrentHashMap<>();
            this.getCharacters().forEach((k, v)->{
                map.put(k,v.clone());
            });
            target.setCharacters(map);
        }
        return target;
    }
}
