package com.nekonade.neko.logic.functionevent;

import com.nekonade.common.db.entity.manager.PlayerManager;
import org.springframework.context.ApplicationEvent;

public class ConsumeDiamond extends ApplicationEvent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int diamond;
    private PlayerManager playerManager;
    public ConsumeDiamond(Object source,int diamond,PlayerManager playerManager) {
        super(source);
        this.diamond = diamond;
        this.playerManager = playerManager;
    }
    public int getDiamond() {
        return diamond;
    }
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    

}
