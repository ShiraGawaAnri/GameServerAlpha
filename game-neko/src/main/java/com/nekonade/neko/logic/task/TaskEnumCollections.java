package com.nekonade.neko.logic.task;

import com.nekonade.dao.db.entity.data.task.BasicTask;
import com.nekonade.dao.db.entity.data.task.ConsumeGoldTask;
import com.nekonade.dao.db.entity.data.task.DayFirstLoginTask;
import lombok.Getter;

public class TaskEnumCollections {

    @Getter
    public enum EnumTaskType {
        DayFirstLogin(1, new AccumulationTaskProgress(), "每日首次登录"),
        ConsumeGold(2, new AccumulationTaskProgress(), "消耗x金币"),
        ConsumeDiamond(3, new AccumulationTaskProgress(), "消耗x钻石"),
        StagePassTimes(5, new SpecificStagePassTimesTaskProgress(), "通关某个关卡多少次"),
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
