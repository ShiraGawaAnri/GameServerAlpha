package com.nekonade.common.db.pojo;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Mail {

    private long id;

    private String senderName;

    private String title;

    private String content;

    private List<Item> gifts;

    private long timestamp;

    private long expired;
}
