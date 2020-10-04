package com.nekonade.center.dataconfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameGatewayInfo {
    private int id; // 唯一id
    private String ip; // 网关ip地址
    private int port; // 网关端口
    private int httpPort;//网关服务的Http的服务地址

    @Override
    public String toString() {
        return "GameGatewayInfo [id=" + id + ", ip=" + ip + ", port=" + port + ", httpPort=" + httpPort + "]";
    }


}
