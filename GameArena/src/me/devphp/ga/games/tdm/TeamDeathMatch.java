package me.devphp.ga.games.tdm;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;

import me.devphp.ga.ArenaInterface;
import me.devphp.ga.Core;
import me.devphp.ga.games.Gamemode;
import me.devphp.teams.TeamManager;


public class TeamDeathMatch implements ArenaInterface{
	public Logger log = Logger.getLogger("Minecraft");
	String arena;
	Core plugin;

	public TeamManager tm;
	private TeamDeathMatchTeamEvent teamEvent;

	public boolean gamestarted = false;
	
	private HashMap<String, PlayerInventory> playerInventory;
	private HashMap<String, Float> playerXp;
	private HashMap<String, Location> playerPreviousLocations;
	
	YamlConfiguration config;
	private boolean hasReady = false;
	
	ScoreTeams scoreTeams = new ScoreTeams();
	
	public TeamDeathMatch(Core plugin, String arena) {
		this.arena		= arena;
		this.plugin		= plugin;
		this.tm			= new TeamManager(this.plugin);
		this.teamEvent	= new TeamDeathMatchTeamEvent(this);
		
		this.config		= this.plugin.getConfig();
		this.reset();
		
		if (this.config.contains("arena." + this.arena + ".team")){
			for (String teamName : this.config.getConfigurationSection("arena." + this.arena + ".team").getKeys(false)){
				this.tm.createTeam(teamName);
			}
			
			if (this.config.contains("arena." + this.arena + ".p2w")){
				this.teamEvent.setPoint2wins(this.config.getInt("arena." + this.arena + ".p2w"));
			}
			// TODO et le temps ?
		}
		// threadRunningGame();
		
		this.tm.setEventHandler(this.teamEvent);
	}
	
	public void reset(){		
		this.playerPreviousLocations	= new HashMap<String, Location>();
		this.playerInventory			= new HashMap<String, PlayerInventory>();
		this.playerXp					= new HashMap<String, Float>();
		
		this.tm.disbandAllTeams();
		if (this.config.contains("arena." + this.arena + ".team")){
			for (String teamName : this.config.getConfigurationSection("arena." + this.arena + ".team").getKeys(false)){
				this.tm.createTeam(teamName);
			}
			
			if (this.config.contains("arena." + this.arena + ".p2w")){
				this.teamEvent.setPoint2wins(this.config.getInt("arena." + this.arena + ".p2w"));
			}
		}
	}
	

	public boolean startGame(){
		// Temps en seconde
		this.broadcastMessage("===== Team Death Match =====");
		this.broadcastMessage("Team Death Match ready. Teleport player on spawn");

		for (String teamName : this.tm.getTeams()){
			for(String player : this.tm.getTeam(teamName).getPlayerList()){
				Player pl = Bukkit.getPlayer(player);
				if (pl != null){
					this.savePlayerPreviousLocation(pl);
					this.saveInventory(pl);
					this.clearStats(pl);
				}
			}
		}
		
		this.teleport();
		
		if (this.config.contains("arena." + this.arena + ".gametime")){
			BukkitTask endTimer = new TeamDeathMatchTimer(this.plugin, this, this.config.getInt("arena." + this.arena + ".gametime")).runTaskTimer(this.plugin, 10, 20);
		}
		return true;
	}
	
	
	public void endGame(){
		this.broadcastMessage("===== Team Death Match =====");
		this.broadcastMessage("Congratulation. The game is now finish. Thanks for participation.");
		this.broadcastScore();

		for (String teamName : this.tm.getTeams()){
			for(String player : this.tm.getTeam(teamName).getPlayerList()){
				Player pl = Bukkit.getPlayer(player);
				if (pl != null){
					if (this.playerPreviousLocations.containsKey(player)){
						pl.teleport(this.playerPreviousLocations.get(player));
						
						pl.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
					}
				}
			}
		}
	}

	
	public void broadcastScore(){
		Map<Integer, String> unsortMap = new HashMap<Integer, String>();
		for (String teamName : tm.getTeams()){
			unsortMap.put(this.scoreTeams.getTeamPoint(teamName), teamName);
		}
		
		Map<Integer, String> treeMap = new TreeMap<Integer, String>(
			new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o2.compareTo(o1);
				}
			}
		);
		treeMap.putAll(unsortMap);
		
		int i = 0;
		for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
			i++;
			this.broadcastMessage(i + ": " + entry.getValue() + " " + entry.getKey() + " pts");
		}
	}
	
	
	/**
	 * Envois un message a tous les joeurs
	 * @param message
	 */
	public void broadcastMessage(String message){
		for (String teamName : this.tm.getTeams()){
			broadcastTeamMessage(teamName, message);
		}
	}
	
	/**
	 * Envois un message a tous les joeurs
	 * @param message
	 */
	public void broadcastTeamMessage(String teamName, String message){
		// Teleport les joueurs a teamSpawn
		for(String player : this.tm.getTeam(teamName).getPlayerList()){
			Player pl = Bukkit.getPlayer(player);
			if (pl != null){
				pl.sendMessage(this.plugin.getPrefix() + message);
			}
		}
	}
	
	/**
	 * Sauvegarde l'inventaire des joueurs
	 */
	private void saveInventory(Player player){		
		this.playerInventory.put(player.getName().toString(),  player.getInventory());
		this.playerXp.put(player.getName().toString(),  player.getExp());
	}

	public void restoreInventory(String player) {
		Player pl = Bukkit.getPlayer(player);
		PlayerInventory PlayerInv = pl.getInventory();

		if (this.playerInventory.get(player) != null) {
			PlayerInv.setContents(this.playerInventory.get(player).getContents());
			PlayerInv.setBoots(this.playerInventory.get(player).getBoots() == null ? null
					: this.playerInventory.get(player).getBoots());
			PlayerInv.setLeggings(this.playerInventory.get(player).getLeggings() == null ? null
					: this.playerInventory.get(player).getLeggings());
			PlayerInv.setChestplate(this.playerInventory.get(player).getChestplate() == null ? null
					: this.playerInventory.get(player).getChestplate());
			PlayerInv.setHelmet(this.playerInventory.get(player).getHelmet() == null ? null
					: this.playerInventory.get(player).getHelmet());

			this.playerInventory.remove(player);
			pl.updateInventory();
			
			pl.setExp(this.playerXp.get(player));
			this.playerXp.remove(player);
		}
	}
	
	private void savePlayerPreviousLocation(Player player){
		this.playerPreviousLocations.put(player.getName().toString(),  player.getLocation());
	}
	
	/**
	 * Reset le infos du joueur, inventaire, armure, mode de jeu, niveau de vie,
	 * niveau de bouff
	 * TODO set armure et arme si possible
	 * 
	 * @param Player
	 */
	public void clearStats(Player player) {
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		player.getWorld().setPVP(true);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFlying(false);
		player.setFoodLevel(20);
		player.setHealth(20.00);
		player.setLevel(0);
	}
	
	private void teleport(){
		for (String teamName : this.tm.getTeams()){
			// Charge l'endroit ou doit etre teleporté le joueur de l'equipe teamName
			Location teamSpawn = new Location(
				Bukkit.getWorld(this.config.getString("arena." + this.arena + ".team." + teamName + ".w")), 
				this.config.getDouble("arena." + this.arena + ".team." + teamName + ".x"),
				this.config.getDouble("arena." + this.arena + ".team." + teamName + ".y"),
				this.config.getDouble("arena." + this.arena + ".team." + teamName + ".z")
			);
			// Teleport les joueurs a teamSpawn
			for(String player : this.tm.getTeam(teamName).getPlayerList()){
				
				Player pl = Bukkit.getPlayer(player);
				if (pl != null){
					pl.teleport(teamSpawn);
				}
			}
		}
	}
	
	@Override
	public boolean join(Player player, String[] args) {		
		int nb = -1;
		String teamName = null;
		
		for (String key : this.tm.getTeams()){
			if (teamName == null){
				teamName = key;
				nb = this.tm.getTeam(key).getPlayerList().length;
			}
			if (this.tm.getTeam(key).getPlayerList().length < nb){
				teamName = key;
				nb = this.tm.getTeam(key).getPlayerList().length;
			}
		}
		
		this.tm.joinTeam(player.getName().toString(), teamName);
		return true;
	}
	
	@Override
	public void sendUsage(Player player) {
		player.sendMessage(this.plugin.getPrefix() + "===== Team Death Match =====");
		player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena set team <team name>" + ChatColor.RESET + " define team name");
//		player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena set spawn <team name>" + ChatColor.RESET + " set spawn location (on your position)");
		player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena set p2w <number of point>" + ChatColor.RESET + " set point needed to wins games");
		player.sendMessage(this.plugin.getPrefix() + "Finish the arena with " + ChatColor.GOLD + "/arena test");
		player.sendMessage(this.plugin.getPrefix() + ChatColor.GOLD + "Happy new game");
	}
	
	@Override
	public String get(String[] args, Player player) throws Exception {

		return "Mmmmh... I am confused , I did not understand your request";
	}

	@Override
	public boolean set(String[] args, Player player) throws Exception {
		
		if (args.length == 3){
			
			switch(args[1].toString()){
				case "team":
					this.config.set("arena." + this.arena + ".team." + args[2].toString() + ".w", player.getLocation().getWorld().getName().toString());
					this.config.set("arena." + this.arena + ".team." + args[2].toString() + ".x", player.getLocation().getX());
					this.config.set("arena." + this.arena + ".team." + args[2].toString() + ".y", player.getLocation().getY());
					this.config.set("arena." + this.arena + ".team." + args[2].toString() + ".z", player.getLocation().getZ());

					player.sendMessage(this.plugin.getPrefix() + "===== Team Death Match =====");
					player.sendMessage(this.plugin.getPrefix() + "Team created and spawn defined");
				break;
				case "p2w":
					if (Integer.valueOf(args[2].toString()) <= 0){
						throw new Exception("Oops argument is not a number or is equal 0");
					}
					
					this.config.set("arena." + this.arena + ".p2w", Integer.valueOf(args[2].toString()));
					
					player.sendMessage(this.plugin.getPrefix() + "===== Team Death Match =====");
					player.sendMessage(this.plugin.getPrefix() + "Point to wins defined");
				break;
				case "gametime":
					if (Integer.valueOf(args[2].toString()) <= 0){
						throw new Exception("Oops argument is not a number or is equal 0");
					}
					
					this.config.set("arena." + this.arena + ".gametime", Integer.valueOf(args[2].toString()));
					
					player.sendMessage(this.plugin.getPrefix() + "===== Team Death Match =====");
					player.sendMessage(this.plugin.getPrefix() + "Game time defined");
				break;
				default:
					this.sendUsage(player);
				break;
			}
		}
		
		return false;
	}

	@Override
	public boolean testing() throws Exception {
		this.log.info("GameArena.testing() called");

		if (!this.config.contains("arena." + this.arena + ".team")){
			throw new Exception("Use " + ChatColor.GOLD + "/arena set team <team name>" + ChatColor.RESET + " define team name");
		}
		
		Set<String> key = this.config.getConfigurationSection("arena." + this.arena + ".team").getKeys(false);
		if (key.size() == 1){
			throw new Exception("Use " + ChatColor.GOLD + "/arena set team <team name>" + ChatColor.RESET + " define team name");
		}
		
		if (!this.config.contains("arena." + this.arena + ".p2w")){
			throw new Exception("Use " + ChatColor.GOLD + "/arena set p2w <number of point>" + ChatColor.RESET + " set point needed to wins games");
		}
		
		if (!this.config.contains("arena." + this.arena + ".gametime")){
			throw new Exception("Use " + ChatColor.GOLD + "/arena set gametime <time in second>" + ChatColor.RESET + " set timeout of the game");
		}
		
		try {
			this.config.save(this.plugin.configFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Save configuration file fail");
		}
		
		this.reset();
		
		return true;
	}

	@Override
	public void setMode(Gamemode mode) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void threadRunningGame(){

	}



	@Override
	public boolean hasReady() {
		// TODO Auto-generated method stub
		return this.hasReady ;
	}

	@Override
	public void leave(Player player) {
		String playerName = player.getName().toString();
		
		if (this.tm.isPlayerInTeam(playerName)){
			try {
				String teamName = this.tm.getPlayerTeam(playerName);
				this.tm.getTeam(teamName).removeFromTeam(playerName);
			} catch (Exception e) {
				log.info(e.getMessage());
				e.printStackTrace();
			}
			
			this.broadcastMessage(player.getName().toString() + " left the game");
		}
	}

}
