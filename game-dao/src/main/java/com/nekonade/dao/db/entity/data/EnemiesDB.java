package com.nekonade.dao.db.entity.data;

import com.mongodb.lang.NonNull;
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
    @AutoIncKey
    private long id;

    @Indexed(unique = true,sparse = true)
    private String monsterId;

    private String name;

    private int key = 1;

    private int maxHp = 100;

    private int target = 0;
}
