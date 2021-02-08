package com.nekonade.dao.db.entity;


import com.nekonade.common.dto.Item;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document("MailBox")
public class MailBox {

//    @Id
//    @AutoIncKey
//    private long id;

    @Indexed
    private long receiverId;

    private long senderId;

    private String senderName;

    private String title;

    private String content;

    private List<Item> gifts;

    private long timestamp;

    private long expired;


}
