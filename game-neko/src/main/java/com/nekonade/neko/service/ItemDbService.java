package com.nekonade.neko.service;

import com.nekonade.dao.db.entity.data.ItemsDB;
import com.nekonade.dao.db.repository.ItemsDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItemDbService {

    @Autowired
    private ItemsDbRepository itemsDbRepository;

    public void addItemDb(ItemsDB itemsDB) {
        itemsDbRepository.save(itemsDB);
    }

    public ItemsDB findById(long id){
        return itemsDbRepository.findById(id).orElse(null);
    }

    public ItemsDB findByItemId(String itemId){
        return itemsDbRepository.findByItemId(itemId).orElse(null);
    }
}
