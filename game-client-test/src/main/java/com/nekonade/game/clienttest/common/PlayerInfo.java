package com.nekonade.game.clienttest.common;

import com.nekonade.common.dto.PlayerDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class PlayerInfo extends PlayerDTO {

    private boolean connected;

    private boolean entered;
}