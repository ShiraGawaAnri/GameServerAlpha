package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
public class UltimateTypesDTO {

    private String typeId;

    private int type;

    private String name;

    private String description;
}
