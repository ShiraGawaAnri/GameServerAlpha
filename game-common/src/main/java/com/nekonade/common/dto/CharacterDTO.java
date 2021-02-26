package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@ToString
public class CharacterDTO implements Cloneable {

    private String charaId;

    private ConcurrentHashMap<String, HeroSkillDTO> skillMap;

    private Integer level = 1;

    private Integer hp = 1;

    private Double speed = 1d;

    private Integer guard = 1;

    private Integer cost = 1;

    private Integer atk = 1;

    private Integer def = 1;

    private UltimateTypes ultimateType;

    private Boolean isNew = false;

    public static class UltimateTypes extends UltimateTypesDTO{

    }

    @Override
    public CharacterDTO clone() {
        CharacterDTO target = new CharacterDTO();
        //先进行简单的浅拷贝
        BeanUtils.copyProperties(this,target);
        {
            ConcurrentHashMap<String, HeroSkillDTO> map = new ConcurrentHashMap<>();
            this.getSkillMap().forEach((k,v)->{
                map.put(k,v.clone());
            });
            target.setSkillMap(map);
        }
        return target;
    }
}
