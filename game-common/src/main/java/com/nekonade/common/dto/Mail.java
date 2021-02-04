package com.nekonade.common.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Mail {

    private long id;

    private String senderName;

    private String title;

    private String content;

    private List<Item> gifts;

    private long timestamp;

    private long expired;
}
