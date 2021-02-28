package com.nekonade.common.error.code;


import com.nekonade.common.error.IServerError;

public enum GameErrorCode implements IServerError {
    HeroNotExist(101, "英雄不存在"),
    WeaponNotExist(102, "武器不存在"),
    HeroLevelNotEnough(103, "魂师等级不足"),
    EquipWeaponCostNotEnough(104, "装备武器消耗不足"),
    WeaponUnenable(105, "武器不可用"),
    HeroHadEquipedWeapon(106, "此英雄已装备武器"),
    StaminaNoEntity(100005, "疲劳值错误"),
    StaminaNotEnough(100006, "疲劳值不足"),
    CharacterExistedCanNotAdd(100010,"角色已拥有,无法新增"),
    DiamondReachMax(10020,"钻石已到达上限,无法添加"),
    StageDbNotFound(100100, "不存在的关卡"),
    StageDbClosed(100101, "关卡未开放"),
    StageReachLimit(100105, "已达到上限次数"),
    StageCostItemNotEnough(100108, "需要消耗的道具数量不足"),
    SingleRaidBattleSameTimeOnlyOne(100200,"同时拥有的单人战斗不能超过1个"),
    MultiRaidBattleSameTimeReachLimit(100201,"同时拥有的战斗不能超过5个"),
    MultiRaidBattlePlayersReachMax(100301,"加入的战斗已满人"),
    MultiRaidBattlePlayersJoinedIn(100302,"已经加入到此战斗中了"),
    MultiRaidBattlePlayerNotJoinedIn(100304,"未加入到此战斗中"),
    RaidBattleHasGone(100305,"此战斗不存在"),
    RaidBattleHasExpired(100306,"此战斗已超时"),
    RaidBattleJoinWithEmptyParty(100307,"必须组成有效的队伍才可加入战斗"),
    SingleRaidNotAcceptOtherPlayer(100311,"无法加入单人战斗"),
    RaidBattleAttackInvalidParam(100320,"无效的攻击"),
    RaidBattleAttackUndefinedSkill(100321,"未定义的卡片技能"),
    LogicError(100500, "请求在服务器内部处理有错误"),
    CoolDownDoReceiveMailBox(100510, "获取邮件道具操作过快"),
    CoolDownDoClaimRaidBattleReward(100511, "获取战斗报酬奖励操作过快"),
    GachaPoolsNotActive(200001,"抽奖池未开放"),
    GachaPoolsDiamondNotEnough(200001,"钻石不足"),
    GachaPoolsNotExist(200404,"抽奖池不存在"),
    GachaPoolsLogicError(200500,"抽奖出现错误"),
    ;
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
