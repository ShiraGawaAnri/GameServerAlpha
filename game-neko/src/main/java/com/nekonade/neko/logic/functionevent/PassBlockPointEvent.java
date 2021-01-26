package com.nekonade.neko.logic.functionevent;

import com.nekonade.dao.db.entity.manager.PlayerManager;
import org.springframework.context.ApplicationEvent;

public class PassBlockPointEvent extends ApplicationEvent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String pointId;
    private PlayerManager playerManager;
    public PassBlockPointEvent(Object source,String pointId,PlayerManager playerManager) {
        super(source);
        this.pointId = pointId;
        this.playerManager = playerManager;
    }
    public String getPointId() {
        return pointId;
    }
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    

}
