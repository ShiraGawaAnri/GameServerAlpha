package com.nekonade.common.basePojo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * @ClassName: BasePlayer
 * @Author: Lily
 * @Description: 基础玩家类
 * @Date: 2021/6/28
 * @Version: 1.0
 */

@Getter
@Setter
public abstract class BasePlayer {

    @Id
    protected long playerId;

    protected String nickName;

    protected Integer level = 1;

}
