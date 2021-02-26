package com.nekonade.neko.service;

import com.nekonade.dao.daos.ItemsDbDao;
import com.nekonade.dao.db.entity.data.ItemsDB;
import com.nekonade.dao.db.repository.ItemsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class ItemDbService {

    @Autowired
    private ItemsDbRepository itemsDbRepository;

    @Autowired
    private ItemsDbDao itemsDbDao;

    public ItemsDB findByItemId(String itemId) {
        /*ItemsDB itemsDB = new ItemsDB();
        itemsDB.setItemId(itemId);
        Optional<ItemsDB> op = itemsDbDao.findByIdInMap(itemsDB, itemId);
        return op.orElse(null);
        */

        ItemsDB result = itemsDbDao.findItemDb(itemId);
        return result;
    }
}
