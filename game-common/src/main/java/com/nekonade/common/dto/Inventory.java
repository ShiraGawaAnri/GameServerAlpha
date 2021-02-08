package com.nekonade.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Inventory {
    //武器包
    protected ConcurrentHashMap<String, Weapon> weaponMap = new ConcurrentHashMap<>();
    //道具包
    protected ConcurrentHashMap<String, Item> itemMap = new ConcurrentHashMap<>();
}
