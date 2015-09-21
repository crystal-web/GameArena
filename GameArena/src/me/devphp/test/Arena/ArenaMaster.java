package me.devphp.test.Arena;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.devphp.test.GameArena;
import me.devphp.test.Arena.MatchMode.tdm.TeamDeathMatch;

public class ArenaMaster implements ArenaInterface {
	private GameArena plugin;
	// Si un mode de jeu est chargé l'arene est prete
	public boolean hasReady = false;
	
	private String arena;
	public ArenaInterface game;

	/**
	 * Charge l'Arene en mode jeu
	 * @param core 
	 * @param arena
	 */
	public ArenaMaster(GameArena core, String arena) {
		this.plugin		= core;
		this.arena		= arena;
		
		if (this.plugin.getConfig().contains("arena." + this.arena + ".mode")){			
			// Regard le mode de jeu a charger			
			if ( !this.setMode(this.plugin.getConfig().getString("arena." + this.arena + ".mode")) ){
				this.plugin.getLog().severe(this.plugin.getPrefixNoColor() + "Unkwon arena match mode has " + this.plugin.getConfig().getString("arena." + this.arena + ".mode"));
			}		
		}
	}
	
	/**
	 * Charge l'Arene en mode conception
	 * @param core
	 * @param arena
	 * @param player
	 */
	public ArenaMaster(GameArena core, String arena, Player player) {
		this.plugin		= core;
		this.arena		= arena;
		
		if (!this.plugin.getConfig().contains("arena." + this.arena + ".mode")){
			player.sendMessage(this.plugin.getPrefix() + "Now use " + ChatColor.GOLD + "/arena mode [match mode]" + ChatColor.RESET + " or " + ChatColor.GOLD + "/arena mode list" + ChatColor.RESET + " to show all match mode");
		} else {
			if ( !this.setMode(this.plugin.getConfig().getString("arena." + this.arena + ".mode")) ){
				player.sendMessage(this.plugin.getPrefix() + "Unknow arena match mode");
				player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena mode [match mode]" + ChatColor.RESET + " or " + ChatColor.GOLD + "/arena mode list" + ChatColor.RESET + " to show all match mode");
			}
		}
		
		this.hasReady	= true;
	}
	
	@Override
	public boolean hasReady(){
		if (this.game != null){
			return this.game.hasReady();
		}
		return this.hasReady;
	}

	@Override
	public boolean setMode(String mode){
		boolean hasTrue = false;
		switch(mode){
			// C'est un TeamDeathMatch
			case "tdm":
				this.plugin.getLog().info("ArenaMaster: Set mode tdm");
				this.game		= new TeamDeathMatch(this.plugin, this.arena);
				hasTrue			= true;
			break;
			default:
				hasTrue			= false;
			break;
		}
		
		if (hasTrue){
			this.plugin.getConfig().set("arena." + this.arena + ".mode", mode);
		}
		
		return hasTrue;
	}
	
	@Override
	public boolean join(Player player, String[] args) throws Exception {
		if (this.hasReady() == false){
			player.sendMessage(this.plugin.getPrefix() + "This arena is not ready");
		}
		
		String playerName = player.getName().toString();
		if (this.plugin.isPlayerInArena( playerName )){
			player.sendMessage(this.plugin.getPrefix() + "You are already in arena (" + this.plugin.getPlayerArena( playerName )+ ")");
			player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena leave" + ChatColor.RESET + " before");
			return false;
		}
		
		if (this.game.join(player, args)){
			this.plugin.setPlayerArena(player.getName().toString(), args[1].toString());
			return true;
		}
		return false;
	}

	@Override
	public void leave(Player player) {
		// on supprime l'enregistrement
//		this.plugin.playerInArena.remove(player.getName().toString());
		this.game.leave(player);
		this.plugin.removePlayerFromArena(player.getName().toString());
	}
	
	@Override
	public String get(String[] args, Player player) throws Exception {
		if (this.hasReady() == false){
			throw new Exception("Arena is not ready");
		}
		if (args.length == 2){
			if (args[1].equalsIgnoreCase("mode")){
				return this.plugin.getConfig().getString("arena." + this.arena + ".mode");
			} else if (args[1].equalsIgnoreCase("name")){
				return this.arena;
			}
		}
		return this.game.get(args, player);
	}

	@Override
	public void set(String[] args, Player player) throws Exception {
		this.game.set(args, player);
	}

	@Override
	public boolean testing() throws Exception {
		if (this.game.testing()){
			this.plugin.arenaList.put(this.arena, new ArenaMaster(this.plugin, this.arena));
			return true;
		}
		return false;
	}

	@Override
	public void sendUsage(Player player) throws Exception {
		if (this.hasReady() == false){
			player.sendMessage("Arena is not ready");
			return;
		}
		this.game.sendUsage(player);
	}

	@Override
	public void threadRunningGame() {
		this.game.threadRunningGame();
	}

	@Override
	public ArenaEventInterface getEvent() {
		return this.game.getEvent();
	}

	@Override
	public ArenaInterface getGame() {
		return this.game.getGame();
	}
}
