package com.nekonade.neko.service;

import com.nekonade.dao.db.entity.Inventory;
import com.nekonade.dao.db.entity.Item;
import com.nekonade.dao.db.entity.data.ItemsDB;
import com.nekonade.network.message.event.function.ItemAddEvent;
import com.nekonade.network.message.event.function.ItemSubEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ItemDbService itemsDbDao;

    private ItemsDB preInventoryItem(Inventory inventory, String itemId) {
        ItemsDB itemsDB = itemsDbDao.findByItemId(itemId);
        if (itemsDB == null) return null;
        inventory.getItemMap().computeIfAbsent(itemId, iid -> {
            Item item = new Item();
            BeanUtils.copyProperties(itemsDB, item);
            item.setCount(0);
            return item;
        });
        return itemsDB;
    }

    @EventListener
    public void itemAddEvent(ItemAddEvent event) {
        Inventory inventory = event.getPlayerManager().getInventoryManager().getInventory();
        String itemId = event.getItemId();
        int count = event.getCount();
        ItemsDB itemsDB = this.preInventoryItem(inventory, itemId);
        if (itemsDB == null) return;
        inventory.getItemMap().computeIfPresent(itemId, (iid, it) -> {
            it.setCount((int) Math.min(itemsDB.getMaxStack(), (it.getCount() + count)));
            return it;
        });
    }

    @EventListener
    public void itemSubEvent(ItemSubEvent event) {
        Inventory inventory = event.getPlayerManager().getInventoryManager().getInventory();
        String itemId = event.getItemId();
        int count = event.getCount();
        ItemsDB itemsDB = this.preInventoryItem(inventory, itemId);
        if (itemsDB == null) return;
        inventory.getItemMap().computeIfPresent(itemId, (iid, it) -> {
            it.setCount(Math.max(0, (it.getCount() - count)));
            return it;
        });
    }
}
