package com.nekonade.common.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeroSkillDTO implements Cloneable{

    private String skillId;

    private Integer level;

    @Override
    protected HeroSkillDTO clone() {
        try{
            return (HeroSkillDTO)super.clone();
        }catch (CloneNotSupportedException e){
            return null;
        }

    }
}
