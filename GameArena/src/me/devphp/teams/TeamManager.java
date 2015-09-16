/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package me.devphp.teams;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.devphp.iPlugin;


/**
 *
 * @author Devphp
 */
public class TeamManager {
	private iPlugin plugin;
	public Logger log = Logger.getLogger("Minecraft");
	
	// les equipes
	private Map<String, Team> teams;
	// le joueur et son équipe (pour les recherches)
	private Map<String, String> playersTeam;
	
	
	private int maxTeamSize;
	
	private String arenaName;
	TeamEvent gameEvent;
	
	
	public TeamManager(iPlugin plugin, TeamEvent game, String arenaName) {
		this.plugin			= plugin;
		this.gameEvent		= game;
		this.arenaName		= arenaName;
		this.teams			= new HashMap<String, Team>();
		this.playersTeam	= new HashMap<String, String>();
		
		
		this.maxTeamSize = 4;
		
		// TODO A tester
		this.plugin
			.getPlugin()
			.getServer()
			.getPluginManager()
			.registerEvents(new PlayerListener(this), (Plugin) this.plugin);
	}
	
	public void defineSpawn(String team, Location location) {
		// TODO Auto-generated method stub
		
	}

	
	/**
	 * retourne la taille maximal d'une equipe
	 * @return int
	 */
	public int getMaxTeamSize() {
		return this.maxTeamSize;
	}
	
	/**
	 * Définit la taille maximal d'une équipe
	 * @param size
	 */
	public void setMaxTeamSize(int size) {
		this.maxTeamSize = size;
	}
	
	/**
	 * Creation d'une équipe
	 * @param teamName
	 */
	public void createTeam(String teamName) {
		teamName = teamName.toLowerCase();
		if (this.teamExists(teamName)) {
			this.log.info(this.plugin.getPrefix() + ChatColor.RED
					+ "Error, this team name already exists...");
			return;
		}
		this.teams.put(teamName, new Team(teamName));
		this.gameEvent.teamCreatedEvent(teamName);
	}
	
	/**
	 * returne la liste des équipes
	 * @return Set<String>
	 */
	public Set<String> getTeams(){
		return this.teams.keySet();
	}

	/**
	 * retourne le nombre de team
	 * @return int
	 */
	public int getTeamCount(){
		return teams.size();
	}
	
	/**
	 * La team existe ?
	 * @param teamName
	 * @return boolean
	 */
	public boolean teamExists(String teamName) {
		teamName = teamName.toLowerCase();
		
		return teams.containsKey(teamName);
	}
	
	/**
	 * retourne l'objet team de la team aillant le nom teamName ou null si elle n'existe pas
	 * @param teamName
	 * @return Team
	 */
	public Team getTeam(String teamName) {
		teamName = teamName.toLowerCase();
		
		if (this.teamExists(teamName)) {
			return this.teams.get(teamName);
		}
		return null;
	}

	/**
	 * savoir si un joueur est dans une équipe
	 * @param playerName
	 * @return boolean
	 */
	public boolean isPlayerInTeam(String playerName) {
		playerName = playerName.toLowerCase();
		return this.playersTeam.containsKey(playerName);
	}
	
	/**
	 * retourne l'equipe du joueur playerName
	 * @param playerName
	 * @return String
	 * @throws Exception
	 */
	public String getPlayerTeam(String playerName) throws Exception{
		playerName = playerName.toLowerCase();
		if (!this.isPlayerInTeam(playerName)){
			throw new Exception("You are not in team");
		}
		return this.playersTeam.get(playerName);
	}


		
	public void joinTeam(String playerName, String teamName) {
		teamName = teamName.toLowerCase();
		playerName = playerName.toLowerCase();
		Team team = this.getTeam(teamName);
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) {
			this.log.severe(this.plugin.getPrefixNoColor() + "joinTeam args addPlayer is null, player is offline ?");
			return;
		}

		if (this.isPlayerInTeam(playerName)) {
			player.sendMessage(this.plugin.getPrefix() + ChatColor.RED
					+ "You are already in a team!");
			return;
		}
		
		if (team.getTeamCount() >= this.maxTeamSize ) {
			player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + "Team "
					+ ChatColor.GOLD + teamName + ChatColor.RESET + " is full.");
			return;
		}

		team.addToTeam(playerName);
		this.playersTeam.put(playerName, teamName);
		
		player.sendMessage(this.plugin.getPrefix() + 
				"You join " + ChatColor.GOLD + teamName + ChatColor.RESET + " !");
		
		this.gameEvent.teamJoinEvent(teamName, playerName);
	}


	
	
	/**
	 * TODO A delete ?
	 * @param sendPlayer
	 * @param message
	 */
	public void chatInTeam(String sendPlayer, String message) {
		sendPlayer = sendPlayer.toLowerCase();
		Player player = Bukkit.getPlayer(sendPlayer);
		if (player == null || !player.isOnline()) {
			return;
		}

		if (playersTeam.containsKey(sendPlayer)) {
			Team team = this.getPlayersTeam(sendPlayer);
			if (team != null) {
				team.sendTeamChat(message, sendPlayer);
			}
		} else {
			player.sendMessage(this.plugin.getPrefix() + ChatColor.RED
					+ "You are not in a party.");
		}
	}

	
	public String[] getPlayersTeamList(String playerName) {
		if (this.isPlayerInTeam(playerName)) {
			return this.getPlayersTeam(playerName).getPlayerList();
		}
		return null;
	}

	public void disbandAllTeam() {
		for (int i = 0; i < teams.size(); i++) {
			Iterator<Team> pit = teams.values().iterator();
			while (pit.hasNext()) {
				Team next = pit.next();
				next.removeAllFromTeam();
				pit.remove();
			}
		}
		teams.clear();
	}

	private Team getPlayersTeam(String playerName) {
		playerName = playerName.toLowerCase();
		if (playersTeam.containsKey(playerName)) {
			return teams.get(playersTeam.get(playerName));
		}
		return null;
	}

	public void sendTeamMessage(String teamName, String message) {
		teamName = teamName.toLowerCase();
		Team team = this.getTeam(teamName);
		if (team != null) {
			team.sendTeamMessage(message);
		}

	}

	public String getPlayersTeamName(String player) {
		player = player.toLowerCase();
		if (playersTeam.containsKey(player)) {
			return playersTeam.get(player);
		}
		return null;
	}

}
