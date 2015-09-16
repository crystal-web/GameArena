package me.devphp.teams;

import org.bukkit.event.entity.PlayerDeathEvent;

public interface TeamEvent {
	public void teamCreatedEvent(String teamName);
	public void teamJoinEvent(String teamName, String playerName);
	public void teamLeaveEvent(String teamName, String playerName);
	
	public void teamDeathEvent(String teamName, String playerName, PlayerDeathEvent event);
	public void teamKillEvent(String teamName, String playerName, PlayerDeathEvent event);	
}
