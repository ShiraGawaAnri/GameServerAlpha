package com.nekonade.common.error.code;


import com.nekonade.common.error.IServerError;

public enum GameErrorCode implements IServerError {
    HeroNotExist(101, "英雄不存在"),
    WeaponNotExist(102, "武器不存在"),
    HeroLevelNotEnough(103, "魂师等级不足"),
    EquipWeaponCostNotEnough(104, "装备武器消耗不足"),
    WeaponUnenable(105, "武器不可用"),
    HeroHadEquipedWeapon(106, "此英雄已装备武器"),
    StaminaNoEntity(100007, "疲劳值错误"),
    StaminaNotEnough(100008, "疲劳值不足"),
    StageDbNotFound(100100, "不存在的关卡"),
    StageDbClosed(100101, "关卡未开放"),
    StageReachLimitCount(100105, "已达到上限次数"),
    StageCostItemNotEnough(100108, "需要消耗的道具数量不足"),
    MultiRaidBattleSameTimeReachLimitCount(100201,"同时拥有的战斗不能超过5个"),
    MultiRaidBattlePlayersReachMax(100301,"加入的战斗已满人"),
    SingleRaidNotAcceptOtherPlayer(100311,"不存在的战斗"),
    LogicError(500, "请求在服务器内部处理有错误");
    private final int errorCode;
    private final String desc;

    GameErrorCode(int errorCode, String desc) {
        this.errorCode = errorCode;
        this.desc = desc;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorDesc() {
        return desc;
    }

}
