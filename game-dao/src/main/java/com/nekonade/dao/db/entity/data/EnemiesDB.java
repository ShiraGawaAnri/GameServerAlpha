package com.nekonade.dao.db.entity.data;

import com.nekonade.dao.seq.AutoIncKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document("EnemiesDB")
public class EnemiesDB {

    @Id
    private String monsterId;

    private String name;

    private Integer exp;

    private Integer level = 1;

    private Integer key = 0;

    private Long hp = 100L;

    private Integer target = 0;

    private Integer atk = 1;

    private Integer def =1;

    private Object element;

    private Double speed = 100d;

    private Integer guard = 100;

    private Object Element;

    private Object ElementLevel;

    private Integer ai;

    private Object drops;

    private List<EnemySkill> skills = new ArrayList<>();

    private String summary;//备注，摘要

    @Getter
    @Setter
    public static class EnemySkill{

        @DBRef
        private ActiveSkillsDB activeSkillsDB;

        private Object condition;
    }
}
