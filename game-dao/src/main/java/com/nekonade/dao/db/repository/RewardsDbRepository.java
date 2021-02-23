package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.RewardsDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RewardsDbRepository extends MongoRepository<RewardsDB,String> {

    void deleteByRewardId(String rewardId);
}
