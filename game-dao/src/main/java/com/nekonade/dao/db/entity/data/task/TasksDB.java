package com.nekonade.dao.db.entity.data.task;

import com.mongodb.lang.NonNull;
import com.nekonade.common.constcollections.EnumCollections;
import com.nekonade.dao.db.entity.data.RewardsDB;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("TasksDB")
public class TasksDB<R extends BasicTask> {

    @Id
    private String taskId;

    @DBRef
    private RewardsDB rewardsDB; //任务奖励id

    private String condition;//SpEL

    @NonNull
    private Integer taskType;

    @NonNull
    private R taskEntity;

    private Integer refreshType = EnumCollections.DataBaseMapper.EnumNumber.No_Refresh.getValue();//刷新类型

}
