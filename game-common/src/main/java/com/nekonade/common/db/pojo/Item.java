package com.nekonade.common.db.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

    protected String id;

    protected int count;

    protected int type;

    protected int category;

    protected Long expired;

}
