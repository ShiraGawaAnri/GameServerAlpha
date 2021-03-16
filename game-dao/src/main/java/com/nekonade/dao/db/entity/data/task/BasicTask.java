package com.nekonade.dao.db.entity.data.task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BasicTask {

    public abstract boolean finishCheck();

    public abstract boolean rewriteCheckFinish();

    public abstract boolean checkParam();

    public abstract Object taskQuota();

    protected Class<?> getEntityClass(){
        return this.getClass();
    }


}
