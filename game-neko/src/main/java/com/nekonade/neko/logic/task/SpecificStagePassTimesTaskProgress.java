package com.nekonade.neko.logic.task;

import com.nekonade.dao.db.entity.Task;
import com.nekonade.dao.db.entity.data.task.SpecificStagePassBlockPointTimeTask;


//通关某个关卡进度值管理
public class SpecificStagePassTimesTaskProgress implements ITaskProgress{

    @Override
    public void updateProgress(Task task, Object data) {
        SpecificStagePassBlockPointTimeTask taskEntity = (SpecificStagePassBlockPointTimeTask) task.getTaskEntity();
        StagePassTimesProgressEntity entity = (StagePassTimesProgressEntity) data;
        StagePassTimesProgressEntity value = (StagePassTimesProgressEntity) task.getValue();
        if(taskEntity.getStageId().equals(entity.getStageId())){
            value.addTime(entity.getTime());
        }
    }

    @Override
    public boolean isFinish(Task task) {
        SpecificStagePassBlockPointTimeTask taskEntity = (SpecificStagePassBlockPointTimeTask) task.getTaskEntity();
        StagePassTimesProgressEntity value = (StagePassTimesProgressEntity) task.getValue();
        return value.getTime() >= taskEntity.getQuota();
    }

    @Override
    public Object getProgressValue(Task task) {
        StagePassTimesProgressEntity value = (StagePassTimesProgressEntity) task.getValue();
        return value.getTime();
    }
}
