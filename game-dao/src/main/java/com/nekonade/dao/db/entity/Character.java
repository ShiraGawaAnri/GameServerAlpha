package com.nekonade.dao.db.entity;

import com.nekonade.common.basePojo.BaseCharacter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;


/**
 * @ClassName: Character
 * @Author: Lily
 * @Description: 玩家拥有的角色实体类
 * @Date: 2021/6/27
 * @Version: 1.0
 */
@Getter
@Setter
@ToString
public class Character extends BaseCharacter implements Cloneable {

    @Id
    protected String id;
}
