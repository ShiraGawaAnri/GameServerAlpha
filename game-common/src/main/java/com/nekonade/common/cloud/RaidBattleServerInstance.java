package com.nekonade.common.cloud;

import com.nekonade.common.error.GameErrorException;
import com.nekonade.common.error.GameGatewayError;
import com.nekonade.common.model.ServerInfo;
import com.nekonade.common.redis.EnumRedisKey;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RaidBattleServerInstance implements ApplicationListener<GameChannelCloseEvent> {

    /**
     * 缓存RaidId对应的处理服务器
     * Map<RaidId,Map<ServiceId,ServerId>>
     */

    private final Map<String, Map<Integer, Integer>> raidBattleServiceInstanceMap = new ConcurrentHashMap<>();

    private final EventExecutor eventExecutor = new DefaultEventExecutor();//

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private BusinessServerService businessServerService;


    public Set<Integer> getAllServiceId() {
        return businessServerService.getAllServiceId();
    }

    public Integer selectRaidBattleServerId(String raidId, int serviceId) {
        Map<Integer, Integer> instanceMap = this.raidBattleServiceInstanceMap.get(raidId);
        Integer serverId = null;
        if (instanceMap != null) {// 如果在缓存中已存在，直接获取对应的serverId
            serverId = instanceMap.get(serviceId);
        } else {// 如果不存在，创建缓存对象
            instanceMap = new ConcurrentHashMap<>();
            this.raidBattleServiceInstanceMap.put(raidId, instanceMap);
        }
        if (serverId != null) {
            if (businessServerService.isEnableServer(serviceId, serverId)) {// 检测目前这个缓存的serverId的实例是否还有效，如果有效，直接返回
                return serverId;
            } else {
                serverId = null;// 如果无效，设置为空，下面再重新获取
            }
        }
        if (serverId == null) {// 重新获取一个新的服务实例serverId
            try {
                String key = this.getRaidBattleRedisKey(raidId);// 从redis查找一下，是否已由别的服务计算好
                key = key.intern();
                synchronized (key){
                    Object value = redisTemplate.opsForValue().get(key);
                    boolean flag = true;
                    if (value != null) {
                        int serverIdOfRedis = Integer.parseInt((String) value);
                        flag = businessServerService.isEnableServer(serviceId, serverIdOfRedis);
                        if (flag) {// 如果redis中已缓存且是有效的服务实例serverId，直接返回
                            this.addRaidBattleLocalCache(raidId, serviceId, serverIdOfRedis);
                            return serverIdOfRedis;
                        }
                    }
                    if (value == null || !flag) {// 如果Redis中没有缓存，或实例已失效，重新获取一个新的服务实例Id
                        Integer serverId2 = this.selectRaidBattleServerIdAndSaveRedis(raidId, serviceId);
                        this.addRaidBattleLocalCache(raidId, serviceId, serverId2);
                        return serverId2;
                    }
                }
            } catch (Throwable e) {
                throw e;
            }
        }
        return serverId;
    }


    public Promise<Integer> selectRaidBattleServerId(String raidId, int serviceId, Promise<Integer> promise) {
        Map<Integer, Integer> instanceMap = this.raidBattleServiceInstanceMap.get(raidId);
        Integer serverId = null;
        if (instanceMap != null) {// 如果在缓存中已存在，直接获取对应的serverId
            serverId = instanceMap.get(serviceId);
        } else {// 如果不存在，创建缓存对象
            instanceMap = new ConcurrentHashMap<>();
            this.raidBattleServiceInstanceMap.put(raidId, instanceMap);
        }
        if (serverId != null) {
            if (businessServerService.isEnableServer(serviceId, serverId)) {// 检测目前这个缓存的serverId的实例是否还有效，如果有效，直接返回
                promise.setSuccess(serverId);
            } else {
                serverId = null;// 如果无效，设置为空，下面再重新获取
            }
        }
        if (serverId == null) {// 重新获取一个新的服务实例serverId
            eventExecutor.execute(() -> {
                try {
                    String key = this.getRaidBattleRedisKey(raidId);// 从redis查找一下，是否已由别的服务计算好
                    key = key.intern();
                    synchronized (key){
                        Object value = redisTemplate.opsForValue().get(key);
                        boolean flag = true;
                        if (value != null) {
                            int serverIdOfRedis = Integer.parseInt((String) value);
                            flag = businessServerService.isEnableServer(serviceId, serverIdOfRedis);
                            if (flag) {// 如果redis中已缓存且是有效的服务实例serverId，直接返回
                                promise.setSuccess(serverIdOfRedis);
                                this.addRaidBattleLocalCache(raidId, serviceId, serverIdOfRedis);
                            }
                        }
                        if (value == null || !flag) {// 如果Redis中没有缓存，或实例已失效，重新获取一个新的服务实例Id
                            Integer serverId2 = this.selectRaidBattleServerIdAndSaveRedis(raidId, serviceId);
                            this.addRaidBattleLocalCache(raidId, serviceId, serverId2);
                            promise.setSuccess(serverId2);
                        }
                    }
                } catch (Throwable e) {
                    promise.setFailure(e);
                }
            });
        }
        return promise;
    }

    private void addRaidBattleLocalCache(String raidId, int serviceId, int serverId) {
        Map<Integer, Integer> instanceMap = this.raidBattleServiceInstanceMap.get(raidId);
        instanceMap.put(serviceId, serverId);// 添加到本地缓存
    }

    private String getRaidBattleRedisKey(String raidId) {
        return EnumRedisKey.RAIDBATTLE_RAIDID_TO_SERVERID.getKey(raidId);
    }

    private Integer selectRaidBattleServerIdAndSaveRedis(String raidId, int serviceId) {
        ServerInfo serverInfo = businessServerService.selectRaidBattleServerInfo(serviceId, raidId);
        if (serverInfo == null) {
            GameGatewayError error = GameGatewayError.GAME_GATEWAY_ERROR;
            GameGatewayError[] values = GameGatewayError.values();
            for (GameGatewayError tempError : values) {
                if (tempError.getErrorCode() == serviceId) {
                    error = tempError;
                    break;
                }
            }
            throw GameErrorException.newBuilder(error).build();
        }
        Integer serverId = serverInfo.getServerId();
        this.eventExecutor.execute(() -> {
            try {
                String key = this.getRaidBattleRedisKey(raidId);
                this.redisTemplate.opsForValue().set(key,String.valueOf(serverId), EnumRedisKey.RAIDBATTLE_RAIDID_TO_SERVERID.getTimeout());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return serverId;
    }

    @Override
    public void onApplicationEvent(GameChannelCloseEvent event) {

    }
}
