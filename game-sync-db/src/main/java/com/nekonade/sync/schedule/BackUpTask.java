package com.nekonade.sync.schedule;

import com.nekonade.sync.GameSyncDb;
import com.nekonade.dao.daos.PlayerDao;
import com.nekonade.dao.redis.EnumRedisKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BackUpTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PlayerDao playerDao;

//    @Override
//    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
//        taskRegistrar.addTriggerTask(
//                //1.添加任务内容(Runnable)
//                () -> {
//                    try {
//                        long start = System.currentTimeMillis();
//                        String patternKey = EnumRedisKey.PLAYER_INFO.getKey()+":*";
//                        ScanOptions options = ScanOptions.scanOptions()
//                                //这里指定每次扫描key的数量(很多博客瞎说要指定Integer.MAX_VALUE，这样的话跟        keys有什么区别？)
//                                .count(10000)
//                                .match(patternKey).build();
//                        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
//                        Cursor cursor = (Cursor) redisTemplate.executeWithStickyConnection(redisConnection -> new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize));
//                        List<String> result = new ArrayList<>();
//                        while(cursor.hasNext()){
//                            result.add(cursor.next().toString());
//                        }
//                        cursor.close();
//                        logger.info("scan扫描共耗时：{} ms key数量：{}",System.currentTimeMillis()-start,result.size());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                },
//                //2.设置执行周期(Trigger)
//                triggerContext -> {
//                    //2.3 返回执行周期(Date)
//                    return new CronTrigger("* * * * * ? *").nextExecutionTime(triggerContext);
//                }
//        );
//    }

    @Scheduled(cron = "0 * * * * ?")
    public void task() {
        try {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            long start = System.currentTimeMillis();
            String patternKey = EnumRedisKey.PLAYER_INFO.getKey()+":*";
            ScanOptions options = ScanOptions.scanOptions()
                    //这里指定每次扫描key的数量(很多博客瞎说要指定Integer.MAX_VALUE，这样的话跟        keys有什么区别？)
                    .count(10000)
                    .match(patternKey).build();
            RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
            Cursor cursor = (Cursor) redisTemplate.executeWithStickyConnection(redisConnection -> new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize));
            List<String> result = new ArrayList<>();
            while(cursor.hasNext()){
                result.add(cursor.next().toString());
            }
            cursor.close();
            logger.info("scan扫描共耗时：{} ms key数量：{}",System.currentTimeMillis()-start,result.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
