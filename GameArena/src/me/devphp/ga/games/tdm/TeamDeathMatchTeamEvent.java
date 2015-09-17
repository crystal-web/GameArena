package me.devphp.ga.games.tdm;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.devphp.teams.TeamEvent;



public class TeamDeathMatchTeamEvent implements TeamEvent {
	private TeamDeathMatch tdm;
	HashMap<String, Integer> teamPoint;
	private int p2w;
	
	public TeamDeathMatchTeamEvent(TeamDeathMatch tdm){
		this.tdm = tdm;
		this.teamPoint = new HashMap<String, Integer>();
	}
	
	public void setPoint2wins(Integer p2w){
		this.p2w = p2w;
	}
	
	@Override
	public void teamCreatedEvent(String teamName) {
		this.teamPoint.put(teamName, 0);
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
	public void teamLeaveEvent(String teamName, String playerName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void teamDeathEvent(String teamName, String playerName, PlayerDeathEvent event) {
		// TODO Auto-generated method stub
		
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
					
					this.incrementPoint(teamName);

					this.tdm.broadcastMessage("[" + ChatColor.GOLD + teamName + ChatColor.RESET + "]"
							+ killer.getName().toString() + " has killed [" + ChatColor.GOLD
							+ this.tdm.tm.getPlayersTeamName(death.getName().toString()) + ChatColor.RESET + "]"
							+ death.getName().toString());
				}
			}
		
		}		
	}
	
	
	private void incrementPoint(String teamName){
		int pt = this.teamPoint.get(teamName);
		pt++;
		this.teamPoint.put(teamName, pt);
		
		if (pt >= this.p2w){
			this.tdm.endGame();
		}
	}

}
