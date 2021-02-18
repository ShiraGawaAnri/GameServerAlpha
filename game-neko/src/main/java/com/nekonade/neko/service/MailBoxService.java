package com.nekonade.neko.service;


import com.nekonade.common.dto.ItemDTO;
import com.nekonade.common.dto.MailDTO;
import com.nekonade.common.model.PageResult;
import com.nekonade.common.utils.FunctionMapper;
import com.nekonade.dao.db.entity.MailBox;
import com.nekonade.dao.db.repository.MailBoxRepository;
import com.nekonade.dao.helper.MongoPageHelper;
import com.nekonade.dao.helper.SortParam;
import com.nekonade.network.message.manager.PlayerManager;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class MailBoxService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoPageHelper mongoPageHelper;

    @Autowired
    private MailBoxRepository mailBoxRepository;

    public PageResult<MailDTO> findByPage(long playerId, int type, Integer page, Integer limit, SortParam sortParam) {
        final Query query = new Query(Criteria.where("receiverId").is(playerId).and("received").is(0));
        Function<MailBox, MailDTO> mapper = FunctionMapper.Mapper(MailBox.class, MailDTO.class);
        return mongoPageHelper.pageQuery(query, MailBox.class, limit, page, sortParam, mapper);
    }

    public List<MailDTO> receiveMailById(PlayerManager dataManager,String mailId){
        long playerId = dataManager.getPlayer().getPlayerId();
        List<MailDTO> list = new ArrayList<>();
        Criteria criteria = Criteria.where("receiverId").is(playerId).and("id").is(mailId).and("received").is(0);
        final Query query = new Query(criteria);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(false);
        options.returnNew(true);
        Update update = new Update();
        update.set("received",1);
        MailBox returnMail = mongoTemplate.findAndModify(query, update, options, MailBox.class);
        if(returnMail != null){
            MailDTO mailDTO = new MailDTO();
            BeanUtils.copyProperties(returnMail,mailDTO);
            list.add(mailDTO);
        }
        return list;
    }

    public List<MailDTO> receiveMailAllPages(PlayerManager dataManager){
        long playerId = dataManager.getPlayer().getPlayerId();
        List<MailDTO> list = new ArrayList<>();
        Criteria criteria = Criteria.where("receiverId").is(playerId).and("received").is(0);
        final Query query = new Query();
        query.addCriteria(criteria);
        List<MailBox> mailBoxes = mongoTemplate.find(query, MailBox.class);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(false);
        options.returnNew(true);
        Update update = new Update();
        update.set("received",1);
        mailBoxes.forEach(mailBox->{
            final Query updateQuery = new Query(Criteria.where("id").is(mailBox.getId()));
            MailBox returnMail = mongoTemplate.findAndModify(updateQuery, update, options, MailBox.class);
            if(returnMail != null && returnMail.getReceived() == 1){
                returnMail.getGifts().forEach(gift->{
                    //TODO: 处理获取物品的溢出部分
                    dataManager.getInventoryManager().produceItem(gift.getItemId(),gift.getAmount());
                });
                MailDTO mailDTO = new MailDTO();
                BeanUtils.copyProperties(returnMail,mailDTO);
                list.add(mailDTO);
            }
        });
        return list;
    }

    public List<MailDTO> receiveMailByPage(PlayerManager dataManager, int page, int type){
        long playerId = dataManager.getPlayer().getPlayerId();
        List<MailDTO> list = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(page, 10);
        Criteria criteria = Criteria.where("receiverId").is(playerId).and("received").is(0);
        final Query query = new Query().with(pageRequest);
        if(type != -1){
            criteria.and("type").is(type);
        }
        query.addCriteria(criteria);
        List<MailBox> mailBoxes = mongoTemplate.find(query, MailBox.class);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(false);
        options.returnNew(true);
        Update update = new Update();
        update.set("received",1);
        mailBoxes.forEach(mailBox -> {
            final Query updateQuery = new Query(Criteria.where("id").is(mailBox.getId()));
            MailBox returnMail = mongoTemplate.findAndModify(updateQuery, update, options, MailBox.class);
            if(returnMail != null && returnMail.getReceived() == 1){
                returnMail.getGifts().forEach(gift->{
                    //TODO: 处理获取物品的溢出部分
                    dataManager.getInventoryManager().produceItem(gift.getItemId(),gift.getAmount());
                });
                MailDTO mailDTO = new MailDTO();
                BeanUtils.copyProperties(returnMail,mailDTO);
                list.add(mailDTO);
            }
        });
        return list;
    }
}
