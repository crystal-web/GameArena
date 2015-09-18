package me.devphp.ga.games.tdm;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.devphp.teams.TeamEvent;



public class TeamDeathMatchTeamEvent implements TeamEvent {
	private TeamDeathMatch tdm;
	private int p2w;
	
	public TeamDeathMatchTeamEvent(TeamDeathMatch tdm){
		this.tdm = tdm;
	}
	
	public void setPoint2wins(Integer p2w){
		this.p2w = p2w;
	}
	
	@Override
	public void teamCreatedEvent(String teamName) {
		this.tdm.scoreTeams.createTeam(teamName);
	}

	@Override
	public void teamJoinEvent(String teamName, String playerName) {
		// test le nombre de joueur est ok 
		int min = this.tdm.tm.getMinTeamSize();
		
		for( String team : this.tdm.tm.getTeams()){
			if (this.tdm.tm.getTeam(team).getTeamCount() < min){
				return;
			}
		}

		this.tdm.startGame();
	}

	@Override
	public void teamKillEvent(String teamName, String playerName, PlayerDeathEvent event) {		
		if (event.getEntity().getKiller() instanceof Player){
			Player killer = event.getEntity().getKiller();
			killer.getWorld().playSound(killer.getLocation(), Sound.FALL_BIG,1, 0);
			// Si le mort est un joueur (il doit aussi etre en jeu)
			if (event.getEntity() instanceof Player){
				Player death = event.getEntity();
				// Ici on évite aussi les nullpointerexception ^^ 
				if (this.tdm.tm.isPlayerInTeam(death.getName().toString())){
					
					// Ajout des points a l'equipe
					this.tdm.scoreTeams.incrementTeamPoint(teamName);
					
					this.tdm.broadcastMessage("[" + ChatColor.GOLD + teamName + ChatColor.RESET + "]"
							+ killer.getName().toString() + " has killed [" + ChatColor.GOLD
							+ this.tdm.tm.getPlayersTeamName(death.getName().toString()) + ChatColor.RESET + "]"
							+ death.getName().toString());

					// Testy si le score permet l'arret de jeu
					if (this.tdm.scoreTeams.getTeamPoint(teamName) >= this.p2w){
						this.tdm.endGame();
					}
				}
			}
		
		}
	}

	@Override
	public void teamRespawnEvent(String teamName, String playerName, PlayerRespawnEvent event) {
		final Player player = event.getPlayer();		
		final Location teamSpawn = new Location(
				Bukkit.getWorld(this.tdm.config.getString("arena." + this.tdm.arena + ".team." + teamName + ".w")), 
				this.tdm.config.getDouble("arena." + this.tdm.arena + ".team." + teamName + ".x"),
				this.tdm.config.getDouble("arena." + this.tdm.arena + ".team." + teamName + ".y"),
				this.tdm.config.getDouble("arena." + this.tdm.arena + ".team." + teamName + ".z")
			);
		
		new BukkitRunnable() {
			  public void run() {
				  player.teleport(teamSpawn);
			  }
			}.runTaskLater(this.tdm.plugin, 20L);
			
		
	}


	
	@Override
	public void teamLeaveEvent(String teamName, String playerName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teamDeathEvent(String teamName, String playerName, PlayerDeathEvent event) {
		// TODO Auto-generated method stub
		
	}
}
