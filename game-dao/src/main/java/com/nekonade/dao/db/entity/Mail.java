package com.nekonade.dao.db.entity;


import com.nekonade.common.db.pojo.Item;
import com.nekonade.dao.redis.EnumRedisKey;
import com.nekonade.dao.seq.AutoIncKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document("Mail")
public class Mail {

    @Id
    @AutoIncKey(use = "redis",key = EnumRedisKey.MAIL_ID_INCR)
    private long id;

    private long senderId;

    private String senderName;

    private String title;

    private String content;

    private List<Item> gifts;

    private long receiverId;

    private long timestamp;

    private long expired;
}
