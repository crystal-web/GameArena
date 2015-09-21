package me.devphp.test.Arena;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public interface ArenaEventInterface {
	
    public void onEntityDamage(EntityDamageEvent event);
    
	public void onPlayerDeathEvent(PlayerDeathEvent event);
	
	public void onPlayerRespawn(PlayerRespawnEvent event);
}
