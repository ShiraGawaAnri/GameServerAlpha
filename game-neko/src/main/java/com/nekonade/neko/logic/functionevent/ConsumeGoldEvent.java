package com.nekonade.neko.logic.functionevent;

import com.nekonade.common.db.entity.manager.PlayerManager;
import org.springframework.context.ApplicationEvent;

public class ConsumeGoldEvent extends ApplicationEvent {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int gold;
    private PlayerManager playerManager;
    public ConsumeGoldEvent(Object source,int gold,PlayerManager playerManager) {
        super(source);
        this.gold = gold;
        this.playerManager = playerManager;
    }
    public int getGold() {
        return gold;
    }
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    
    
    

}
