package me.devphp.teams;

import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public interface TeamEvent {
	public void teamCreatedEvent(String teamName);
	public void teamJoinEvent(String teamName, String playerName);
	public void teamLeaveEvent(String teamName, String playerName);
	
	public void teamRespawnEvent(String teamName, String playerName, PlayerRespawnEvent event);
	public void teamDeathEvent(String teamName, String playerName, PlayerDeathEvent event);
	public void teamKillEvent(String teamName, String playerName, PlayerDeathEvent event);	
}
