package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserAccountRepository extends MongoRepository<UserAccount, Long> {
    
     UserAccount findByUserName(String userName);
}
