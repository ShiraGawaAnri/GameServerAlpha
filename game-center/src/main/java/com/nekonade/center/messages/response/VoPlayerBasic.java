package com.nekonade.center.messages.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoPlayerBasic {
    
    private long playerId;
    private String nickName;
    private int level;
    private long lastLoginTime;//上次登陆时间
   
}
