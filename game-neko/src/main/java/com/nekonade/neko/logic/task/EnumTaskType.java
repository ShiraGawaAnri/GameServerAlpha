package com.nekonade.neko.logic.task;

public enum EnumTaskType {
    ConsumeGold(1, new AccumulationTaskProgress(), "消耗x金币"),
    ConsumeDiamond(2, new AccumulationTaskProgress(), "消耗x钻石"),
    PassBlockPoint(3, new SpecificBlockTaskProgress(), "通关某个关卡"),
    PassBlockPointTimes(4, new SpecificBlockTimesTaskProgress(), "通关某个关卡多少次"),
    ;
    private final int type;
    private final ITaskProgress taskProgress;
    private final String desc;

    EnumTaskType(int type, ITaskProgress taskProgress, String desc) {
        this.type = type;
        this.taskProgress = taskProgress;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public ITaskProgress getTaskProgress() {
        return taskProgress;
    }

    public String getDesc() {
        return desc;
    }
}
