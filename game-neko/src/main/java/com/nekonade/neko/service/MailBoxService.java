package com.nekonade.neko.service;


import com.nekonade.common.db.pojo.Mail;
import com.nekonade.dao.db.repository.MailBoxRepository;
import com.nekonade.dao.helper.MongoPageHelper;
import com.nekonade.common.model.PageResult;
import com.nekonade.dao.helper.SortParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailBoxService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoPageHelper mongoPageHelper;

    @Autowired
    private MailBoxRepository mailBoxRepository;

    public PageResult<Mail> findByPage(long playerId, List<Integer> filter, Integer page, Integer limit, SortParam sortParam){
        final Query query = new Query(Criteria.where("receiverId").is(playerId));
        return mongoPageHelper.pageQuery(query,Mail.class,limit,page,sortParam);
    }
}
