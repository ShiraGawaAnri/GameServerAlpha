package com.nekonade.neko.logic.task;

import com.nekonade.dao.daos.db.TasksDbDao;
import com.nekonade.dao.db.entity.Task;
import com.nekonade.dao.db.entity.data.task.TasksDB;
import com.nekonade.network.message.event.function.EnterGameEvent;
import com.nekonade.network.message.manager.TaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TaskService {


    @Autowired
    private TasksDbDao tasksDbDao;

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @EventListener
    public void EnterGameEvent(EnterGameEvent event) {
        // 进入游戏的时候，判断一下任务有没有实始化，没有初始化的，自动接收第一个任务
        TaskManager taskManager = event.getPlayerManager().getTaskManager();
        Map<String, Task> tasks = taskManager.getTasks();
        Map<String, TasksDB> maps = tasksDbDao.findAllInMap();
        //得到差集
        tasks.keySet().forEach(maps::remove);
        //移除不在任务DB中的任务 - 注意这必须得至少是一个ConcurrentHashmap
        for(String taskId:tasks.keySet()){
            if(!maps.containsKey(taskId)){
                taskManager.removeTask(taskId);
            }
        }
        //添加差集部分
        maps.entrySet().stream().forEach(it->{
            String taskId = it.getKey();
            TasksDB entity = it.getValue();
            Task task = new Task();
            task.setTaskId(taskId);
            task.setTaskType(entity.getTaskType());
            task.setTaskEntity(entity.getTaskEntity());
        });
    }

    @EventListener
    public void LoginTask(EnterGameEvent event){
        TaskManager taskManager = event.getPlayerManager().getTaskManager();
        Map<String, Task> tasks = taskManager.getTasks();
        tasks.values().forEach(task->{
            if(!task.isClear()){
                updateTaskProgress(TaskEnumCollections.EnumTaskType.DayFirstLogin,task,1);
                boolean clear = checkTaskIsFinish(TaskEnumCollections.EnumTaskType.DayFirstLogin,task);
                if(clear){
                    task.setClear(true);
                }
            }
        });
    }

    /*@EventListener
    public void consumeGold(ConsumeGoldEvent event) {
        // 接收金币消耗事件
        this.updateTaskProgress(event.getPlayerManager().getTaskManager(), TaskEnumCollections.EnumTaskType.ConsumeGold, event.getGold());
    }

    @EventListener
    public void consumeDiamond(ConsumeDiamond event) {
        this.updateTaskProgress(event.getPlayerManager().getTaskManager(), TaskEnumCollections.EnumTaskType.ConsumeDiamond, event.getDiamond());
    }*/

    /*@EventListener
    public void passBlockPoint(PassBlockPointEvent event) {
        //通关事件影响多个任务类型的进度
        this.updateTaskProgress(event.getPlayerManager().getTaskManager(), TaskEnumCollections.EnumTaskType.PassBlockPoint, event.getPointId());
        this.updateTaskProgress(event.getPlayerManager().getTaskManager(), TaskEnumCollections.EnumTaskType.PassBlockPointTimes, event.getPointId());
    }*/

    private void updateTaskProgress(TaskEnumCollections.EnumTaskType taskType,Task task, Object value) {
        if (task.getTaskType() == taskType.getType()) {//如果事件更新的任务类型，与当前接受的任务类型一致，更新任务进度
            taskType.getTaskProgress().updateProgress(task, value);
        }
    }

    private boolean checkTaskIsFinish(TaskEnumCollections.EnumTaskType taskType,Task task) {
        if (task.getTaskType() == taskType.getType()) {//如果事件更新的任务类型，与当前接受的任务类型一致，更新任务进度
           return taskType.getTaskProgress().isFinish(task);
        }
        return false;
    }

    /*
    public GlobalConfig.TaskConfig getTaskDataConfig(String taskId) {
        // 根据taskId获取这个taskId对应的配置数据，这里直模拟返回一个
        return new GlobalConfig.TaskConfig();
    }*/

    public TaskEnumCollections.EnumTaskType getEnumTaskType(int taskType) {
        for (TaskEnumCollections.EnumTaskType enumTaskType : TaskEnumCollections.EnumTaskType.values()) {
            if (enumTaskType.getType() == taskType) {
                return enumTaskType;
            }
        }
        throw new IllegalArgumentException("任务类型不存在：" + taskType);
    }
}
