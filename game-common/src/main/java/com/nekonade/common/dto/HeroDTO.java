package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class HeroDTO implements Cloneable {

    private String heroId;

    private ConcurrentHashMap<String, HeroSkillDTO> skillMap;

    private int level;

    private String weaponId;

    @Override
    public HeroDTO clone() {
        HeroDTO target = new HeroDTO();
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
