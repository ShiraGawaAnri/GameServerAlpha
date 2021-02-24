package com.nekonade.dao.db.entity.data;

import com.nekonade.dao.seq.AutoIncKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("EnemiesDB")
public class EnemiesDB {

    @Id
    private String monsterId;

    private String name;

    private Integer key = 0;

    private Integer maxHp = 100;

    private Integer target = 0;
}
