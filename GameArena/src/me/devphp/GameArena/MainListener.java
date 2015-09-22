package me.devphp.GameArena;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import me.devphp.GameArena.Arena.ArenaEventInterface;
import me.devphp.GameArena.Arena.ArenaInterface;

/**
 * On écoute les evenements et envois ceux-ci à ArenaEventInterface
 * Toute class qui implement ArenaEventInterface execute l'evenement.
 * 
 * @author Devphp
 *
 */
public class MainListener implements Listener {
	
	private GameArena plugin;

	public MainListener(GameArena plugin){
		this.plugin = plugin;
		this.plugin.getLog().info("MainListener ready");
	}
	
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()){
        	return;
        }

        if (event.getEntity() instanceof Player){
        	String playerName = event.getEntity().getName().toString();
	    	if (this.plugin.isPlayerInArena(playerName)){
	    		ArenaInterface ai = this.plugin.getArena( this.plugin.getPlayerArena(playerName) );
	    		if (ai instanceof ArenaInterface){    			
	    			ArenaEventInterface eventInterface = ai.getEvent();
	    			if (eventInterface == null){
	    				this.plugin.getLog().severe("ArenaEventInterface return null for " + this.plugin.getArena( this.plugin.getPlayerArena(playerName) ));
	    			} else if (ai.getEvent() instanceof ArenaEventInterface){
	    				eventInterface.onEntityDamage(event);		
	    			}
	    		}
	    	}
        }
    }
    
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event){
    	String playerName = event.getEntity().getName().toString();
    	if (this.plugin.isPlayerInArena(playerName)){
    		ArenaInterface ai = this.plugin.getArena( this.plugin.getPlayerArena(playerName) );
    		if (ai instanceof ArenaInterface){    			
    			ArenaEventInterface eventInterface = ai.getEvent();
    			if (eventInterface == null){
    				this.plugin.getLog().severe("ArenaEventInterface return null for " + this.plugin.getArena( this.plugin.getPlayerArena(playerName) ));
    			} else if (ai.getEvent() instanceof ArenaEventInterface){
    				eventInterface.onPlayerDeathEvent(event);    			
    			}
    		}
    	}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
    	String playerName = event.getPlayer().getName().toString();
    	if (this.plugin.isPlayerInArena(playerName)){
    		ArenaInterface ai = this.plugin.getArena( this.plugin.getPlayerArena(playerName) );
    		if (ai instanceof ArenaInterface){    			
    			ArenaEventInterface eventInterface = ai.getEvent();
    			if (eventInterface == null){
    				this.plugin.getLog().severe("ArenaEventInterface return null for " + this.plugin.getArena( this.plugin.getPlayerArena(playerName) ));
    			} else if (ai.getEvent() instanceof ArenaEventInterface){
    				eventInterface.onPlayerRespawn(event);   			
    			}
    		}
    	}
	}
}
