package me.devphp.test.Arena.MatchMode.tdm;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.devphp.teams.Team;
import me.devphp.test.Arena.ArenaEventInterface;

public class TeamDeathMatchEvent implements ArenaEventInterface {

	private TeamDeathMatch arena;
	private int points2wins;

	public TeamDeathMatchEvent(TeamDeathMatch arena){
		this.arena	= arena;
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (!this.arena.hasReady()){return;}
	}

	@Override
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		if (!this.arena.hasReady()){return;}
		
		try {
			
			if (event.getEntity().getKiller() instanceof Player){
				Player killer = event.getEntity().getKiller();
				killer.getWorld().playSound(killer.getLocation(), Sound.FALL_BIG,1, 0);
				String killerName = killer.getName().toString();
				
				if (!this.arena.getTeamManager().isPlayerInTeam(killerName)){
					return;
				}
				String killerTeam = this.arena.getTeamManager().getPlayerTeam(killerName);
	
				// Si le mort est un joueur (il doit aussi etre en jeu)
				if (event.getEntity() instanceof Player){
					Player death = event.getEntity();
					String deathName = death.getName().toString();
					// Ici on évite aussi les nullpointerexception ^^ 
					if (this.arena.getTeamManager().isPlayerInTeam(deathName)){
						
						this.arena.getScoreTeams().incrementTeamPoint(killerTeam);
						this.arena.getPlayerStat(killerName).kill();
						this.arena.getPlayerStat(deathName).death();
						// For example 
						// this.arena.getScoreTeams().addTeamPoint(killerTeam, 10);
						
						event.setDeathMessage(
								this.arena.getPlugin().getPrefix()
								+ ChatColor.GRAY + "[" + ChatColor.GREEN + this.arena.getArenaName() + ChatColor.GRAY + "] " + ChatColor.RESET
								+ "[" + ChatColor.GOLD + killerTeam + ChatColor.RESET + "]"
								+ killerName + " has killed [" + ChatColor.GOLD
								+ this.arena.getTeamManager().getPlayerTeamName(deathName) + ChatColor.RESET + "]"
								+ death.getName().toString());
						/*
						this.arena.getTeamManager().broadcast("[" + ChatColor.GOLD + killerTeam + ChatColor.RESET + "]"
								+ killerName + " has killed [" + ChatColor.GOLD
								+ this.arena.getTeamManager().getPlayerTeamName(deathName) + ChatColor.RESET + "]"
								+ death.getName().toString());								
						 */
						
						
						// Testy si le score permet l'arret de jeu
						if (this.arena.getScoreTeams().getTeamPoint(killerTeam) >= this.points2wins){
							this.arena.endGame();
						}
					}
				}
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		
		if (!this.arena.getTeamManager().isPlayerInTeam( player.getName().toString() )){
			return;
		}
		
		try {
			String teamName = this.arena.getTeamManager().getPlayerTeam( player.getName().toString() );
			if (teamName == null){
				this.arena.getPlugin().getLog().info("TeamDeathMatchEvent.onPlayerRespawn teamName == null");
			} else {
				final Location teamLocation = this.arena.getTeamSpawn(teamName);
				if (teamLocation == null){
					this.arena.getPlugin().getLog().info("TeamDeathMatchEvent.onPlayerRespawn teamLocation == null");
				} else {
					new BukkitRunnable() {
						public void run() {
							try {
								player.teleport(teamLocation);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.runTaskLater(this.arena.getPlugin(), 40L);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public void setPoint2wins(int points) {
		this.points2wins = points;
	}

}
