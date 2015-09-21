/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package me.devphp.teams;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.devphp.iPlugin;


/**
 * teamJoinEvent ok
 * teamCreateEvent ok
 * 
 * @author Devphp
 */
public class TeamManager {
	private iPlugin plugin;
	
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
	}
	
	public void setEventHandler(TeamEvent event){
		this.gameEvent		= event;
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
		if (this.teamExists(teamName)) {
			this.plugin.getLog().info("Error, this team name already exists...");
			return;
		}
		this.teams.put(teamName, new Team(teamName));
		if (this.gameEvent != null){
			this.plugin.getLog().info("GameEvent.teamCreatedEvent is not null send event");
			this.gameEvent.teamCreatedEvent(teamName);
		}
	}
	
	/**
	 * La team existe ?
	 * @param teamName
	 * @return boolean
	 */
	public boolean teamExists(String teamName) {
		return teams.containsKey(teamName);
	}
	
	/**
	 * returne la liste des équipes
	 * @return Set<String>
	 */
	public Set<String> getTeamList(){
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
		Team team = this.getTeam(teamName);
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) {
			this.plugin.getLog().severe("joinTeam args addPlayer is null, player is offline ?");
			return;
		}

		if (this.isPlayerInTeam(playerName)) {
			player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + "You are already in a team!");
			return;
		}
		
		if (team.getTeamCount() >= this.maxTeamSize ) {
			player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + "Team " + ChatColor.GOLD + teamName + ChatColor.RESET + " is full.");
			return;
		}

		this.playersTeam.put(playerName, teamName);
		
		player.sendMessage(this.plugin.getPrefix() + "You join " + ChatColor.GOLD + teamName + ChatColor.RESET + " !");
		team.addToTeam(player);
		
		if (this.gameEvent != null){
			this.plugin.getLog().info("GameEvent.teamJoinEvent is not null send event");
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
		return this.playersTeam.containsKey(playerName);
	}
	
	/**
	 * retourne l'equipe du joueur playerName
	 * @param playerName
	 * @return String
	 * @throws Exception
	 */
	public String getPlayerTeam(String playerName) throws Exception{
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
	public Map<String, Player> getPlayersTeamList(String playerName) {
		if (this.isPlayerInTeam(playerName)) {
			return this.getPlayersTeam(playerName).getPlayerList();
		}
		return null;
	}

	public Team getPlayersTeam(String playerName) {
		if (playersTeam.containsKey(playerName)) {
			return teams.get(playersTeam.get(playerName));
		}
		return null;
	}

	public String getPlayerTeamName(String playerName) {
		if (playersTeam.containsKey(playerName)) {
			return playersTeam.get(playerName);
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
		Player player = Bukkit.getPlayer(sendPlayer);
		if (player == null || !player.isOnline()) {
			return;
		}

		if (playersTeam.containsKey(sendPlayer)) {
			Team team = this.getPlayersTeam(sendPlayer);
			if (team != null) {
				this.plugin.getLog().info("TeamManager.sendTeamChat is not null send");
				team.sendTeamChat(message, sendPlayer);
			}
		} else {
			player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + "You are not in a party.");
		}
	}
	
	public void sendTeamMessage(String teamName, String message) {
		Team team = this.getTeam(teamName);
		if (team != null) {
			this.plugin.getLog().info("TeamManager.sendTeamMessage is not null send");
			team.sendTeamMessage(message);
		}
	}

	public void broadcast(String message) {
		for (String teamName : this.teams.keySet()){
			this.sendTeamMessage(teamName, message);
		}
	}

}
