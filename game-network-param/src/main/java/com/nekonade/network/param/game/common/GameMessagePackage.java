package com.nekonade.network.param.game.common;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameMessagePackage {

    private GameMessageHeader header;

    private byte[] body;

}
