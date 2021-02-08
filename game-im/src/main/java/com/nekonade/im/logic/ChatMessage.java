package com.nekonade.im.logic;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessage {

    private long playerId;
    private String nickName;
    private String chatMessage;


}
