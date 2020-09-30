package com.nekonade.network.message.game;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @ClassName: HeaderAttribute
 * @Description: 做为包头的扩展类，因为网关是转发消息的，有些客户端的数据也需要一起转发出去，为了扩展时，不修改协议的编码和解码，都放在这个类里面
 *               这个类在序列化为会使用json序列化。
 */
@Getter
@Setter
public class HeaderAttribute {

    private String clientIp;//客户端ip
    @Override
    public String toString() {
        return "HeaderAttribute [clientIp=" + clientIp + "]";
    }
    
    
    
}
