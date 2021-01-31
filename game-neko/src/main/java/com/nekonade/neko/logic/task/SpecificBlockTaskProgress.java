package com.nekonade.neko.logic.task;


import com.nekonade.network.message.manager.TaskManager;
import com.nekonade.neko.dataconfig.TaskDataConfig;

/**
 * 
 * @ClassName: SpecificBlockTaskProgress 
 * @Description: 通关到指定关卡的进度类
 * @author: wgs
 * @date: 2019年7月3日 上午10:22:43
 */
public class SpecificBlockTaskProgress implements ITaskProgress{

    @Override
    public void updateProgress(TaskManager taskManager, TaskDataConfig taskDataConfig, Object data) {
        taskManager.setValue((String)data);
    }

    @Override
    public boolean isFinish(TaskManager taskManager, TaskDataConfig taskDataConfig) {
        String value = taskManager.getTaskStringValue();
        if(value == null) {
            return false;
        }
        return value.compareTo(taskDataConfig.param) >= 0;//如果当前关卡大于等于目标关卡，说明已通关
    }

    @Override
    public Object getProgessValue(TaskManager taskManager, TaskDataConfig taskDataConfig) {
        return taskManager.getTaskStringValue();
    }

}
