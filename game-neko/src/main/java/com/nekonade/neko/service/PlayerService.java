package com.nekonade.neko.service;


import com.nekonade.common.db.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerService {

	private ConcurrentHashMap<Long, Player> playerCache = new ConcurrentHashMap<Long, Player>();
	
	public Player getPlayer(Long playerId) {
		return playerCache.get(playerId);
	}
	
	public void addPlayer(Player player) {
		this.playerCache.putIfAbsent(player.getPlayerId(), player);
	}
	
}
