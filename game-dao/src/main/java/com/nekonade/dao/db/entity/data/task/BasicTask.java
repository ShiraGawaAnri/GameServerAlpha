package com.nekonade.dao.db.entity.data.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;




@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY)
/*@JsonSubTypes({
        @JsonSubTypes.Type(value = DayFirstLoginTask.class, name = "DayFirstLoginTask"),
        @JsonSubTypes.Type(value = ConsumeGoldTask.class, name = "ConsumeGoldTask")
})*/
public abstract class BasicTask {

    public abstract boolean finishCheck();

    public abstract boolean rewriteCheckFinish();

    public abstract boolean checkParam();

    public abstract Object taskQuota();

    protected Class<?> getEntityClass(){
        return this.getClass();
    }


}
