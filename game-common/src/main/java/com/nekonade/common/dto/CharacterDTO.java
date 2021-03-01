package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@ToString
public class CharacterDTO extends RaidBattleTarget implements Cloneable {

    private String charaId;

    private ConcurrentHashMap<String, HeroSkillDTO> skillMap;

    private Integer cost = 1;

    private Boolean isNew = false;

    @Override
    public int sourceType() {
        return EnumDTO.SourceType.RaidBattle_SourceType_Player.getType();
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
