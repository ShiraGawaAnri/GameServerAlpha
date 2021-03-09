package com.nekonade.game.clienttest.test;

import com.nekonade.game.clienttest.common.ClientPlayerInfo;
import com.nekonade.game.clienttest.common.PlayerInfo;
import com.nekonade.game.clienttest.common.RaidBattleInfo;
import com.nekonade.game.clienttest.service.GameClientBoot;
import com.nekonade.game.clienttest.service.GameClientInitService;
import lombok.Getter;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class ThreadContainer {

    @Getter
    private final static ThreadLocal<ClientPlayerInfo> clientPlayerInfo = ThreadLocal.withInitial(ClientPlayerInfo::new);

    @Getter
    private final static ThreadLocal<RaidBattleInfo> raidBattleInfo = ThreadLocal.withInitial(RaidBattleInfo::new);

    @Getter
    private final static ThreadLocal<PlayerInfo> playerInfo = ThreadLocal.withInitial(PlayerInfo::new);



}
