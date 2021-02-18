package com.nekonade.neko.service;


import com.nekonade.common.dto.MailDTO;
import com.nekonade.common.model.PageResult;
import com.nekonade.common.utils.FunctionMapper;
import com.nekonade.dao.db.entity.MailBox;
import com.nekonade.dao.db.repository.MailBoxRepository;
import com.nekonade.dao.helper.MongoPageHelper;
import com.nekonade.dao.helper.SortParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

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

    public PageResult<MailDTO> findByPage(long playerId, List<Integer> filter, Integer page, Integer limit, SortParam sortParam) {
        final Query query = new Query(Criteria.where("receiverId").is(playerId));
//        Function<MailBox, Mail> mapper = mailBox -> {
//            Mail mail = new Mail();
//            mail.setTitle(mailBox.getTitle());
//            mail.setId(mailBox.getId());
//            mail.setSenderName(mailBox.getSenderName());
//            mail.setContent(mailBox.getSenderName());
//            mail.setGifts(mailBox.getGifts());
//            mail.setTimestamp(mailBox.getTimestamp());
//            mail.setExpired(mailBox.getExpired());
//            return mail;
//        };
        Function<MailBox, MailDTO> mapper = FunctionMapper.Mapper(MailBox.class, MailDTO.class);
        return mongoPageHelper.pageQuery(query, MailBox.class, limit, page, sortParam, mapper);
//        return mongoPageHelper.pageQuery(query, MailBox.class,limit,page,sortParam,null,Mail.class);
    }
}
