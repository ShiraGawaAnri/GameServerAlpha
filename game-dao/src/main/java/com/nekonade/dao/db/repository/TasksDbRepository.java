package com.nekonade.dao.db.repository;

import com.nekonade.dao.db.entity.data.task.TasksDB;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TasksDbRepository extends MongoRepository<TasksDB,String> {
}
