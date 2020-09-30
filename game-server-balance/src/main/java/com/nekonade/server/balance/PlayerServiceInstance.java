package com.nekonade.server.balance;

import com.nekonade.server.balance.model.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class PlayerServiceInstance {
	/**
	 * 缓存PlayerID对应的所有的服务的实例的id,最外层的key是playerId，里面的Map的key是serviceId，value是serverId
	 */
	private final Map<Long, Map<Integer, Integer>> serviceInstanceMap = new ConcurrentHashMap<>();
	
	private final BusinessServerService businessServerService;
	private final StringRedisTemplate redisTemplate;
	private final ApplicationContext context;
	private static final Logger logger = LoggerFactory.getLogger(PlayerServiceInstance.class);
	
	public PlayerServiceInstance(ApplicationContext context) {
		this.context = context;
		businessServerService = context.getBean(BusinessServerService.class);
		redisTemplate = context.getBean(StringRedisTemplate.class);
	}
	@PostConstruct
	public void init() {
		logger.info("初始化PlayerServiceInstance:{},{}",businessServerService == null,context == null);
	}
	public Set<Integer> getAllServiceId() {
		return businessServerService.getAllServiceId();
	}
	public int selectServerId(Long playerId, int serviceId,String namespace) {
		Map<Integer, Integer> instanceMap = this.serviceInstanceMap.get(playerId);
		int serverId = 0;
		if (instanceMap != null) {// 如果在缓存中已存在，直接获取对应的serverId
			serverId = instanceMap.get(serviceId);
		} else {// 如果不存在，创建缓存对象
			instanceMap = new ConcurrentHashMap<>();
			this.serviceInstanceMap.put(playerId, instanceMap);
		}
		if (serverId != 0) {
			if (!businessServerService.isEnableServer(serviceId, serverId)) {// 检测目前这个缓存的serverId的实例是否还有效，如果有效，直接返回
				serverId = 0;// 如果无效，设置为空，下面再重新获取
			}
		}
		if (serverId == 0) {// 重新获取一个新的服务实例serverId
			String key = this.getRedisKey(playerId,namespace);// 从redis查找一下，是否已由别的服务计算好
			Object value = redisTemplate.opsForHash().get(key, String.valueOf(serviceId));
			boolean flag = true;
			if (value != null) {
				serverId = Integer.parseInt((String) value);
				flag = businessServerService.isEnableServer(serviceId, serverId);
				if (flag) {// 如果redis中已缓存且是有效的服务实例serverId，直接返回
					this.addLocalCache(playerId, serviceId, serverId);
					return serverId;
				}
			}
			if (value == null || !flag) {// 如果Redis中没有缓存，或实例已失效，重新获取一个新的服务实例Id
				serverId = this.selectServerIdAndSaveRedis(playerId, serviceId,namespace);
				this.addLocalCache(playerId, serviceId, serverId);
				return serverId;
			}
		}
		return serverId;
	}

	private void addLocalCache(long playerId, int serviceId, int serverId) {
		Map<Integer, Integer> instanceMap = this.serviceInstanceMap.get(playerId);
		instanceMap.put(serviceId, serverId);// 添加到本地缓存
	}
	private String getRedisKey(Long playerId,String namespace) {
		return "service_instance_" + namespace + "_" + playerId;
	}
	private Integer selectServerIdAndSaveRedis(Long playerId, Integer serviceId,String namespace) {
		ServerInfo serverInfo = businessServerService.selectServerInfo(serviceId, playerId);
		if(serverInfo == null) {
			logger.error("找不到游戏服务器信息,playerID:{},serviceID:{}",playerId,serviceId);
			return 0;
		}
		Integer serverId = businessServerService.selectServerInfo(serviceId, playerId).getServerId();
		try {
			String key = this.getRedisKey(playerId,namespace);
			this.redisTemplate.opsForHash().put(key, String.valueOf(serviceId), String.valueOf(serverId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverId;
	}
	
	public void remove(long playerId) {
		this.serviceInstanceMap.remove(playerId);
	}

}
