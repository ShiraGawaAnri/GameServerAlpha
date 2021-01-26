package com.nekonade.neko.logic.task;

import com.nekonade.dao.db.entity.manager.TaskManager;
import com.nekonade.neko.dataconfig.TaskDataConfig;

//数值累计型进度值管理
public class AccumulationTaskProgress implements ITaskProgress{
    @Override
    public void updateProgress(TaskManager taskManager, TaskDataConfig taskDataConfig, Object data) {
        taskManager.addValue((int)data);//更新任务进度
    }

    @Override
    public boolean isFinish(TaskManager taskManager, TaskDataConfig taskDataConfig) {
        int target = Integer.parseInt(taskDataConfig.param);
        int value = taskManager.getTaskIntValue();
        return value >= target;//判断任务是否完成
    }

    @Override
    public Object getProgessValue(TaskManager taskManager, TaskDataConfig taskDataConfig) {
        return taskManager.getTaskIntValue();//获取任务进度
    }

}
