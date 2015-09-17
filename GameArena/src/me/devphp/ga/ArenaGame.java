package me.devphp.ga;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.devphp.ga.games.Gamemode;
import me.devphp.ga.games.tdm.TeamDeathMatch;

public class ArenaGame implements ArenaInterface {
	// Si un mode de jeu est chargé l'arene est prete
	private boolean hasReady = false;
	
	private String arena;
	public ArenaInterface game;

	private Core plugin;

	/**
	 * Charge l'Arene en mode jeu
	 * @param core 
	 * @param arena
	 */
	public ArenaGame(Core core, String arena) {
		this.plugin		= core;
		this.arena		= arena;
		
		if (this.plugin.getConfig().contains("arena." + this.arena + ".mode")){			
			// Regard le mode de jeu a charger
			switch(this.plugin.getConfig().getString("arena." + this.arena + ".mode").toLowerCase()){
				// C'est un TeamDeathMatch
				case "tdm":
					this.game		= new TeamDeathMatch(this.plugin, this.arena);
					this.hasReady	= true;
				break;
				default:
					this.hasReady 	= false;
				break;
			}
		}
	}
	
	/**
	 * Charge l'Arene en mode conception
	 * @param core
	 * @param arena
	 * @param player
	 */
	public ArenaGame(Core core, String arena, Player player) {
		this.plugin		= core;
		this.arena		= arena;
		
		player.sendMessage(this.plugin.getPrefix() + "========== Arena ==========");
		if (this.plugin.getConfig().contains("arena." + this.arena + ".mode")){
			
			// Regard le mode de jeu a charger
			switch(this.plugin.getConfig().getString("arena." + this.arena + ".mode").toLowerCase()){
				// C'est un TeamDeathMatch
				case "tdm":
					player.sendMessage(this.plugin.getPrefix() + "Arena has loaded, has TeamDeathMAtch");
					this.game	= new TeamDeathMatch(this.plugin, this.arena);
				break;
				default:
					player.sendMessage(this.plugin.getPrefix() + "Arena mode unknown for " + this.arena + ". Check configuration file Arena.yml");
					this.plugin.getLogger().info(this.plugin.getPrefixNoColor() + "Arena mode unknown for " + this.arena + ". Check configuration file Arena.yml");
				break;
			}
		} else {
			player.sendMessage(this.plugin.getPrefix() + "Now use " + ChatColor.GOLD + "/arena mode [game mode]" + ChatColor.RESET + " or " + ChatColor.GOLD + "/arena mode list" + ChatColor.RESET + " to show all game mode");
		}
	}

	
	@Override
	public boolean hasReady(){
		return this.hasReady;
	}

	@Override
	public void setMode(Gamemode mode){ 
		this.plugin.getConfig().set("arena." + this.arena + ".mode", mode.getTag());
		this.game	= new TeamDeathMatch(this.plugin, this.arena);
	}
	
	@Override
	public boolean join(Player player, String[] args) throws Exception {
		if (this.hasReady() == false){
			throw new Exception("Arena is not ready");
		}
		return this.game.join(player, args);
	}

	@Override
	public String get(String[] args, Player player) throws Exception {
		if (this.hasReady() == false){
			throw new Exception("Arena is not ready");
		}
		if (args.length == 2){
			if (args[1].equalsIgnoreCase("mode")){
				return this.plugin.config.getString("arena." + this.arena + ".mode");
			} else if (args[1].equalsIgnoreCase("name")){
				return this.arena;
			}
		}
		return this.game.get(args, player);
	}

	@Override
	public boolean set(String[] args, Player player) throws Exception {
		return this.game.set(args, player);
	}

	@Override
	public boolean testing() throws Exception {
		if (this.game.testing()){
			this.plugin.games.put(this.arena, new ArenaGame(this.plugin, this.arena));
			return true;
		}
		return false;
	}

	@Override
	public void sendUsage(Player player) throws Exception {
		if (this.hasReady() == false){
			throw new Exception("Arena is not ready");
		}
		this.game.sendUsage(player);
	}

	@Override
	public void threadRunningGame() {
		this.game.threadRunningGame();
	}
}
