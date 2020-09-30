package com.nekonade.dao.db.entity;

import com.nekonade.dao.db.entity.model.PlayerBasic;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Player")
public class Player extends PlayerBasic {
    
    private boolean newCreate;//是否是新创建的.

    public boolean isNewCreate() {
        return newCreate;
    }

    public void setNewCreate(boolean newCreate) {
        this.newCreate = newCreate;
    }
    
    
}
