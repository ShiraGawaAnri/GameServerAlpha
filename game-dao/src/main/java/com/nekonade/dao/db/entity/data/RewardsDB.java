package com.nekonade.dao.db.entity.data;

import com.nekonade.common.draw.DrawProb;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@Document("RewardsDB")
public class RewardsDB {

    @Id
    private String rewardId;

    private List<Item> items = new LinkedList<>();

    @Getter
    @Setter
    public static class Item extends DrawProb {

        private String itemId;

        private Integer type = 0;

        private Integer category = 0;

        private Integer rewardItemId = 0;

        private Integer amount = 0;

        private Boolean randomAmount = false;

        private Integer randomAmountMin = 0;

        private Integer randomAmountMax = 1;

    }

    public void makeItem(){
        this.items.sort(Comparator.comparingInt(Item::getCategory).thenComparingInt(Item::getType).thenComparing(Item::getItemId));
        AtomicInteger i = new AtomicInteger(0);
        this.items.forEach(item->{
            item.setRewardItemId(i.get());
            i.getAndIncrement();
        });
    }
}
