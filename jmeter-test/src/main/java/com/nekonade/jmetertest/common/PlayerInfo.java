package com.nekonade.jmetertest.common;

import com.nekonade.common.dto.CharacterDTO;
import com.nekonade.common.dto.PlayerDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Component
public class PlayerInfo extends PlayerDTO {
    
    private boolean connected;

    private boolean entered;
}
