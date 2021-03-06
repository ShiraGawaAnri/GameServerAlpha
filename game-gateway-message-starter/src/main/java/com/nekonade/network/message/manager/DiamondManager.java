package com.nekonade.network.message.manager;

import com.nekonade.common.error.GameErrorException;
import com.nekonade.common.error.code.GameErrorCode;
import com.nekonade.dao.daos.GlobalConfigDao;
import com.nekonade.dao.db.entity.Diamond;
import com.nekonade.dao.db.entity.config.GlobalConfig;
import lombok.Getter;
import org.springframework.context.ApplicationContext;

public class DiamondManager {

    private final PlayerManager playerManager;

    private final ApplicationContext context;

    private final GlobalConfigDao globalConfigDao;

    @Getter
    private final Diamond diamond;

    public DiamondManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
        this.context = playerManager.getContext();
        this.diamond = playerManager.getPlayer().getDiamond();
        this.globalConfigDao = playerManager.getContext().getBean(GlobalConfigDao.class);
    }

    public boolean checkDiamondEnough(int needAmount){
        return this.diamond.getAmount() >= needAmount;
    }

    public boolean checkDiamondMaxValue(int addAmount){
        return this.diamond.getAmount() + addAmount >= globalConfigDao.getGlobalConfig().getDiamond().getMaxValue();
    }

    public void addDiamond(int amount){
        if(checkDiamondMaxValue(amount)){
            throw GameErrorException.newBuilder(GameErrorCode.DiamondReachMax).build();
        }
        this.diamond.setAmount(this.diamond.getAmount() + amount);
    }

    public void subDiamond(int amount){
        this.diamond.setAmount(this.diamond.getAmount() - amount);
    }
}
