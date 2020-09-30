package com.nekonade.server.balance;

import com.nekonade.server.balance.model.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 网关后面的业务服务管理
 * 
 * @ClassName: BusinessServerService
 * @Description: TODO
 * @author: wgs
 * @date: 2019年5月5日 上午10:11:38
 */
public class BusinessServerService implements ApplicationListener<HeartbeatEvent> {
	private static final Logger logger = LoggerFactory.getLogger(BusinessServerService.class);
	@Autowired
	private DiscoveryClient discoveryClient;
	private Map<Integer, List<ServerInfo>> serverInfos; // serviceId对应的服务器集合，一个服务可能部署到多台服务器上面，实现负载均衡

	@PostConstruct
	public void init() {
		logger.info("初始化,{},{}", this.getClass().getName(), discoveryClient == null);
		this.refreshBusinessServerInfo();
	}

	public Set<Integer> getAllServiceId() {
		return serverInfos.keySet();
	}

	private void refreshBusinessServerInfo() {// 刷新网关后面的服务列表
		Map<Integer, List<ServerInfo>> tempServerInfoMap = new HashMap<>();
		List<String> services = discoveryClient.getServices();
		for (String serviceId : services) {
			List<ServiceInstance> businessServiceInstances = discoveryClient.getInstances(serviceId);// 网取网关后面的服务实例
			businessServiceInstances.forEach(instance -> {
				int weight = this.getServerInfoWeight(instance);
				for (int i = 0; i < weight; i++) {
					ServerInfo serverInfo = this.newServerInfo(instance);
					if (serverInfo == null) {
						continue;
					}
					List<ServerInfo> serverList = tempServerInfoMap.get(serverInfo.getServiceId());
					if (serverList == null) {
						serverList = new ArrayList<>();
						tempServerInfoMap.put(serverInfo.getServiceId(), serverList);
					}
					serverList.add(serverInfo);
				}
			});
		}
		this.serverInfos = tempServerInfoMap;
	}

	public ServerInfo selectServerInfo(Integer serviceId, Long playerId) {// 从游戏网关列表中选择一个游戏服务实例信息返回。
		// 再次声明一下，防止游戏网关列表发生变化，导致数据不一致。
		Map<Integer, List<ServerInfo>> serverInfoMap = this.serverInfos;
		List<ServerInfo> serverList = serverInfoMap.get(serviceId);
		if (serverList == null || serverList.size() == 0) {
			return null;
		}
		int hashCode = Math.abs(playerId.hashCode());
		int gatewayCount = serverList.size();
		int index = hashCode % gatewayCount;
		if (index >= gatewayCount) {
			index = gatewayCount - 1;
		}
		return serverList.get(index);
	}

	/**
	 * 判断某个服务中的serverId是否还有效
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param serviceId
	 * @param serverId
	 * @return
	 * @author wgs
	 * @date 2019年5月18日 下午6:20:59
	 *
	 */
	public boolean isEnableServer(Integer serviceId, Integer serverId) {
		Map<Integer, List<ServerInfo>> serverInfoMap = this.serverInfos;
		List<ServerInfo> serverInfoList = serverInfoMap.get(serviceId);
		if (serverInfoList != null) {
			return serverInfoList.stream().anyMatch(c -> {
				return c.getServerId() == serverId;
			});
		}
		return false;

	}

	private ServerInfo newServerInfo(ServiceInstance instance) {
		String serviceId = instance.getMetadata().get("serviceId");
		String serverId = instance.getMetadata().get("serverId");
		if (StringUtils.isEmpty(serviceId)) {
			logger.info("加载实例：{}时，此实例未配置serviceId", instance.getServiceId());
			return null;
		}

		if (StringUtils.isEmpty(serverId)) {
			logger.info("加载实例：{}时，此实例未配置serverId", instance.getServiceId());
			return null;
		}
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setServiceId(Integer.parseInt(serviceId));
		serverInfo.setServerId(Integer.parseInt(serverId));
		serverInfo.setHost(instance.getHost());
		serverInfo.setPort(instance.getPort());

		return serverInfo;
	}

	private int getServerInfoWeight(ServiceInstance instance) {
		String value = instance.getMetadata().get("weight");
		if (value == null) {
			value = "1";
		}
		return Integer.parseInt(value);
	}

	@Override
	public void onApplicationEvent(HeartbeatEvent event) {
		this.refreshBusinessServerInfo();
	}
}
