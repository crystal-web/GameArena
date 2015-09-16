package me.devphp.teams;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerListener implements Listener {

	private TeamManager teamManager;

	public PlayerListener(TeamManager tm){
		this.teamManager = tm;
	}
	
	

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()){
        	return;
        }

        if(event instanceof EntityDamageByEntityEvent)
        {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            Entity sender = e.getDamager();
            Entity reciever = e.getEntity();
            if(sender instanceof Player && reciever instanceof Player)
            {
                Player attacker = (Player) sender;
                Player defender = (Player) reciever;
                
                if(this.teamManager.isPlayerInTeam(defender.getName()) && this.teamManager.isPlayerInTeam(attacker.getName()))
                {
                    if(this.teamManager.getPlayersTeamName(defender.getName())
                    		.equals(this.teamManager.getPlayersTeamName(attacker.getName()))
                    )
                    {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    
	@EventHandler
	public void playerDeath(PlayerDeathEvent event){
		if (event.getEntity().getKiller() instanceof Player){
			Player killer = event.getEntity().getKiller();
			if ( this.teamManager.isPlayerInTeam(killer.getName().toString()) ){
				killer.getWorld().playSound(killer.getLocation(), Sound.FALL_BIG,1, 0);
				this.teamManager.gameEvent.teamKillEvent(this.teamManager.getPlayersTeamName(killer.getName().toString()), killer.getName().toString(), event);
				
				if (event.getEntity() instanceof Player){
					Player death = event.getEntity();
					this.teamManager.gameEvent.teamDeathEvent(this.teamManager.getPlayersTeamName(death.getName().toString()), death.getName().toString(), event);
				}
			}
		}
	}
}
