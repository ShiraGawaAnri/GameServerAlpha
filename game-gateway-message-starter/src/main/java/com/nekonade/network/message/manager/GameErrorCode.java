package com.nekonade.network.message.manager;


import com.nekonade.common.error.IServerError;

public enum GameErrorCode implements IServerError {
    HeroNotExist(101,"英雄不存在"),
    WeaponNotExist(102,"武器不存在"),
    HeroLevelNotEnough(103,"魂师等级不足"),
    EquipWeaponCostNotEnough(104,"装备武器消耗不足"),
    WeaponUnenable(105,"武器不可用"),
    HeroHadEquipedWeapon(106,"此英雄已装备武器"),
    StaminaNoEntity(100007,"疲劳值错误"),
    StageDbNotFound(100100,"不存在的关卡")
    ;
    private final int errorCode;
    private final String desc;
    
    GameErrorCode(int errorCode, String desc){
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
