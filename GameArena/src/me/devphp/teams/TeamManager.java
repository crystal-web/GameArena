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
 * teamJoinEvent ok
 * teamCreateEvent ok
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
	
	private int minTeamSize;
	private int maxTeamSize;
	TeamEvent gameEvent;
	
	
	public TeamManager(iPlugin plugin) {
		this.plugin			= plugin;
		this.teams			= new HashMap<String, Team>();
		this.playersTeam	= new HashMap<String, String>();
		
		this.minTeamSize	= 1;
		this.maxTeamSize	= 4;
		
		// TODO A tester
		this.plugin
			.getPlugin()
			.getServer()
			.getPluginManager()
			.registerEvents(new TeamPlayerListener(this), (Plugin) this.plugin);
	}
	
	public void setEventHandler(TeamEvent event){
		this.gameEvent		= event;
	}
	
	public void defineSpawn(String team, Location location) {
		// TODO Auto-generated method stub
	}

	/**
	 * Propriété
	 */
	
	/**
	 * retourne la taille maximal d'une equipe
	 * @return int
	 */
	public int getMinTeamSize() {
		return this.minTeamSize;
	}
	
	/**
	 * Définit la taille maximal d'une équipe
	 * @param size
	 */
	public void setMinTeamSize(int size) {
		this.minTeamSize = size;
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
	 * Team
	 */
	
	/**
	 * Retire toute les équipes
	 */
	public void disbandAllTeams() {
		for (int i = 0; i < teams.size(); i++) {
			Iterator<Team> pit = teams.values().iterator();
			while (pit.hasNext()) {
				Team next = pit.next();
				next.removeAllFromTeam();
				pit.remove();
			}
		}
		teams.clear();
		this.teams			= new HashMap<String, Team>();
		this.playersTeam	= new HashMap<String, String>();
	}
	
	/**
	 * Creation d'une équipe
	 * @param teamName
	 */
	public void createTeam(String teamName) {
		teamName = teamName.toLowerCase();
		if (this.teamExists(teamName)) {
			this.log.info(this.plugin.getPrefixNoColor() + 
					"Error, this team name already exists...");
			return;
		}
		this.teams.put(teamName, new Team(teamName));
		if (this.gameEvent != null){
			this.gameEvent.teamCreatedEvent(teamName);
		}
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
	
	public Team getTeamFromPlayerName(String playerName) {	
		if (this.isPlayerInTeam(playerName)) {
			return this.teams.get(this.getPlayersTeam(playerName));
		}
		return null;
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

		
		this.playersTeam.put(playerName, teamName);
		
		player.sendMessage(this.plugin.getPrefix() + 
				"You join " + ChatColor.GOLD + teamName + ChatColor.RESET + " !");
		team.addToTeam(playerName);
		
		if (this.gameEvent != null){
			this.gameEvent.teamJoinEvent(teamName, player);
		}
	}
	
	/**
	 * Player team
	 */
	
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

	/**
	 * Retourne la liste des joueurs dans la même équipe que playerName
	 * @param playerName
	 * @return
	 */
	public String[] getPlayersTeamList(String playerName) {
		if (this.isPlayerInTeam(playerName)) {
			return this.getPlayersTeam(playerName).getPlayerList();
		}
		return null;
	}

	private Team getPlayersTeam(String playerName) {
		playerName = playerName.toLowerCase();
		if (playersTeam.containsKey(playerName)) {
			return teams.get(playersTeam.get(playerName));
		}
		return null;
	}

	public String getPlayersTeamName(String player) {
		player = player.toLowerCase();
		if (playersTeam.containsKey(player)) {
			return playersTeam.get(player);
		}
		return null;
	}
	
	/**
	 * Chat & discution
	 */
	
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
	
	public void sendTeamMessage(String teamName, String message) {
		teamName = teamName.toLowerCase();
		Team team = this.getTeam(teamName);
		if (team != null) {
			team.sendTeamMessage(message);
		}

	}

}
