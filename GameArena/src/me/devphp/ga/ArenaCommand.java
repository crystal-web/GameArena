package me.devphp.ga;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.devphp.ga.games.Gamemode;

/**
 * Cette classe s'occupe de traiter les requêtes de création d'arene
 * Elle charge l'arene en mémoire jusqu'au passage du test de fonctionnement.
 * 
 * @author Devphp
 *
 */
public class ArenaCommand implements CommandExecutor{
	
	private Core plugin;
	private Map<String, ArenaGame> arena;
	
	public ArenaCommand(Core plugin){
		this.plugin = plugin;
		this.arena = new HashMap<String, ArenaGame>();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("arena")){
			return false;
		}
		if (!(sender instanceof Player)){
			this.plugin.getLogger().info("You are not a player");
			return false;
		}
		Player player = (Player) sender;

//		player.sendMessage(this.plugin.getPrefix() + "========== Arena Debug ==========");
//		player.sendMessage(this.plugin.getPrefix() + "Arguments " + args.length);
		
		/**
		 * Utilisation coté joueur
		 */
		if (args[0].equalsIgnoreCase("join")){
			if (args.length < 2){
				player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena list" + ChatColor.RESET + " for list all arena available.");
				player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena join <arena name>" + ChatColor.RESET + " to join arena");
				return true;
			}
			
			if (!this.plugin.games.containsKey(args[1].toString())){
				player.sendMessage(this.plugin.getPrefix() + "Unknown arena");
				player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena list" + ChatColor.RESET + " for list all arena available.");
				return true;
			}
			
			ArenaInterface curArena = this.plugin.games.get(args[1].toString());
			
			try {
				curArena.join(player, args);
			} catch (Exception e) {
				player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + e.getMessage());
			}
			return true;
		} else if (args[0].equalsIgnoreCase("list")){
			player.sendMessage(this.plugin.getPrefix() + "========== Arena ==========");
			
			if (this.plugin.games.size() == 0){
				player.sendMessage(this.plugin.getPrefix() + "Never arena found");
				return true;
			}
		
			for(String game : this.plugin.games.keySet()){
				
				ArenaInterface ar = this.plugin.games.get(game);
				
				String[] name = { 
						"get",
					    "name"
					};
				String[] mode = { 
						"get",
					    "mode"
					};
				
				try {
					player.sendMessage(
						this.plugin.getPrefix() + 
						ChatColor.GOLD + 
						ar.get(name, player) 
						+ " " + 
						ChatColor.RESET + 
						Gamemode.get( ar.get(mode, player) ).toString() 
					);
					
				} catch (Exception e) {
					this.plugin.getLogger().info( "Exception on read list of arena: " + e.getMessage() );
				}
				
			}
			
			
			return true;
		}
		
		
		/**
		 * Custom & creation
		 */
		
		/**
		 * Test des permissions
		 */
		if (
				(
					!player.hasPermission("gamearena.creator") || !player.isOp()
				)
					&& 
				(
					args[0].equalsIgnoreCase("create")	||	args[0].equalsIgnoreCase("load") || 
					args[0].equalsIgnoreCase("set")		||	args[0].equalsIgnoreCase("get") ||
					args[0].equalsIgnoreCase("mode") 
				)
				
			){
			player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + "You dont have permission \"gamecore.simplegame.creator\"");
			return false;
		}
		
		/**
		 * Test si l'arene est chargé avant les actions sur elle
		 */
		if (
				!this.arena.containsKey(player.getName().toString()) && 
				( 
					args[0].equalsIgnoreCase("set")		||	
					args[0].equalsIgnoreCase("get")		||
					args[0].equalsIgnoreCase("mode")	||
					args[0].equalsIgnoreCase("test")
				)
			){
			player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena [create|load] <arena name>" + ChatColor.RESET + " before.");
			return true;
		}
		
		/**
		 * Pour le custom, 2 arguments nécéssaire
		 */
		if (
				args.length < 2 && 
				( 
					args[0].equalsIgnoreCase("set")		||	
					args[0].equalsIgnoreCase("get")		||
					args[0].equalsIgnoreCase("mode")
				)
			){
			this.usage(player);
			return false;
		}
		
		/**
		 * TODO Ici j'aurai voulu un switch, mais ne fonctionne pas ou provoque une erreur de syntaxe
		 * Pourquoi ?  
		 */
		if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("load")){
			this.arena.put(player.getName().toString(), new ArenaGame(this.plugin, args[1].toLowerCase(), player));
			return true;

		/**
		 * Définition du mode de jeu
		 */
		} else if (args[0].equalsIgnoreCase("mode")){			
			if (args[1].equalsIgnoreCase("list")){
				player.sendMessage(this.plugin.getPrefix() + "========== Arena ==========");
				
				for (Gamemode mode : Gamemode.values()) {
					if (mode.isEnabled()){
						player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + mode.getTag() + ChatColor.RESET + " for " + ChatColor.GOLD + mode.toString());
					}
				}
				
				return true;
			}
			
			if (!Gamemode.contains( args[1].toString() )){
				player.sendMessage(this.plugin.getPrefix() + "Game mode not found");
				return true;
			}
			
			ArenaGame megame = this.arena.get(player.getName().toString());
			megame.setMode(Gamemode.get(args[1].toString()));
			try {
				megame.game.sendUsage(player);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return true;
			
		/**
		 * Définition des paramètres
		 */
		} else if (args[0].equalsIgnoreCase("set")) {			
			ArenaGame set = this.Arena( player.getName().toString() );
			try {
				return set.set(args, player);
			} catch (Exception e) {
				player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + e.getMessage());
			}
			
			return true;
			
		/**
		 * Lecture des définitions
		 */
		} else if (args[0].equalsIgnoreCase("get")) {
			ArenaGame get = this.Arena( player.getName().toString() );
			try {
				player.sendMessage(this.plugin.getPrefix() + get.get(args, player));
				return true;
			} catch (Exception e) {
				player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + e.getMessage());
			}
			
			return true;

		/**
		 * Test de la configuration et enregistrement des données
		 */
		} else if (args[0].equalsIgnoreCase("test")){
			if ( !this.arena.containsKey( player.getName().toString() ) ){
				this.usage(player);
				return false;
			}
			
			ArenaGame test = this.Arena( player.getName().toString() );
			try {
				this.plugin.getLogger().info("Call testing for " + player.getName().toString());
				if (test.testing()){
					player.sendMessage(this.plugin.getPrefix() + ChatColor.GREEN + "Arena ready");
				} else {
					player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + "Arena not ready");
				}
			} catch (Exception e) {
				e.printStackTrace();
				player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + e.getMessage());
			}
			
			return true;
		}
		
		this.usage(player);
		return false;
	}
	
	/**
	 * Retourne l'objet Arene de l'areneName
	 * @param arenaName
	 * @return
	 */
	private ArenaGame Arena(String player){
		return this.arena.get( player );
	}
	
	/**
	 * Affiche l'aide exprese pour l'utilisation
	 * Pas fini, du tout ^^
	 * @param player
	 */
	private void usage(Player player) {
		player.sendMessage(this.plugin.getPrefix() + ChatColor.GREEN + "========== SimpleGame ===========");
		player.sendMessage(this.plugin.getPrefix() + ChatColor.GREEN + "Usage: ");
	}

}
