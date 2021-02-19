package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.MailBox;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MailBoxRepository extends MongoRepository<MailBox, Long> {

}
