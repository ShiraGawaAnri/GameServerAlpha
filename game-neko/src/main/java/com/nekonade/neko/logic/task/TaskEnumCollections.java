package com.nekonade.neko.logic.task;

import com.nekonade.dao.db.entity.data.task.BasicTask;
import com.nekonade.dao.db.entity.data.task.ConsumeGoldTask;
import com.nekonade.dao.db.entity.data.task.DayFirstLoginTask;
import lombok.Getter;

public class TaskEnumCollections {

    @Getter
    public enum EnumTaskType {
        ConsumeGold(1, new AccumulationTaskProgress(), "消耗x金币",ConsumeGoldTask.class),
        ConsumeDiamond(2, new AccumulationTaskProgress(), "消耗x钻石",null),
        PassBlockPoint(3, null, "通关某个关卡",null),
        PassBlockPointTimes(4, null, "通关某个关卡多少次",null),
        DayFirstLogin(5, new AccumulationTaskProgress(), "每日首次登录", DayFirstLoginTask.class),
        ;
        private final int type;
        private final ITaskProgress taskProgress;
        private final String desc;
        private final Class<? extends BasicTask> clazz;

        EnumTaskType(int type, ITaskProgress taskProgress, String desc, Class<? extends BasicTask> clazz) {
            this.type = type;
            this.taskProgress = taskProgress;
            this.desc = desc;
            this.clazz = clazz;
        }
    }
}
