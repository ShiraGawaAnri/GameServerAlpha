package com.nekonade.dao.db.entity.data;

import com.nekonade.common.enums.EnumEntityDB;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document("RaidBattleEffectGroupsDB")
public class RaidBattleEffectGroupsDB {

    @Id
    private String effectGroupId;

    private int groupOverlapping = EnumEntityDB.EnumNumber.RaidBattle_EffectGroups_Overlapping.getValue();//是否允许叠加 0:取最大值 1:允许

    private double groupMaxStackValue = 50.0D;//同一组buff/debuff时,最大上限

//    @DBRef
//    private RaidBattleEffectGroupType groupType;

    private String description;
}
