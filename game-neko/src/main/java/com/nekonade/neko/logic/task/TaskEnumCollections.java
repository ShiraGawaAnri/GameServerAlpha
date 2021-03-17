package com.nekonade.neko.logic.task;

import com.nekonade.dao.db.entity.data.task.BasicTask;
import com.nekonade.dao.db.entity.data.task.ConsumeGoldTask;
import com.nekonade.dao.db.entity.data.task.DayFirstLoginTask;
import lombok.Getter;

public class TaskEnumCollections {

    @Getter
    public enum EnumTaskType {
        ConsumeGold(1, new AccumulationTaskProgress(), "消耗x金币"),
        ConsumeDiamond(2, new AccumulationTaskProgress(), "消耗x钻石"),
        PassBlockPoint(3, null, "通关某个关卡"),
        PassBlockPointTimes(4, null, "通关某个关卡多少次"),
        DayFirstLogin(5, new AccumulationTaskProgress(), "每日首次登录"),
        ;
        private final int type;
        private final ITaskProgress taskProgress;
        private final String desc;

        EnumTaskType(int type, ITaskProgress taskProgress, String desc) {
            this.type = type;
            this.taskProgress = taskProgress;
            this.desc = desc;
        }
    }
}
