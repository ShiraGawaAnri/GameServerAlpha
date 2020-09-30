package com.nekonade.dao.db.entity;

import com.nekonade.dao.db.entity.model.PlayerBasic;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "UserAccount")
@Getter
@Setter
public class UserAccount {
    @Indexed
    private String userName;
    private String password;
    @Id
    private long userId;
    private long createTime;
    private String registIp;
    // 登陆类型：1 用户名密码；2 第三方sdk
    private int registerType;
    private String lastLoginIp;
    // 记录已创建角色的基本信息
    private List<PlayerBasic> players = new ArrayList<>();
   

}
