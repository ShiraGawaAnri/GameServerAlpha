package com.nekonade.center.messages.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class VoUserAccount {

    private String userName;
    private long userId;
    private long createTime;
    private String registIp;
    // 登陆类型：1 用户名密码；2 第三方sdk
    private int registerType;
    private String lastLoginIp;
    // 记录已创建角色的基本信息
    private List<VoPlayerBasic> players = new ArrayList<>();
    
    private String token;
    
    
}
