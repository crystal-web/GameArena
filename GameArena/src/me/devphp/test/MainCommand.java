package me.devphp.test;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.devphp.teams.Team;
import me.devphp.test.Arena.ArenaInterface;
import me.devphp.test.Arena.ArenaMaster;
import me.devphp.test.Arena.MatchMode.MatchMode;
import me.devphp.test.Arena.MatchMode.tdm.TeamDeathMatch;

/**
 * Cette classe s'occupe de traiter les requêtes de création d'arene et pour les rejoindre
 * Elle charge l'arene en mémoire jusqu'au passage du test de fonctionnement.
 * 
 * @author Devphp
 *
 */
public class MainCommand implements CommandExecutor{
	
	private GameArena plugin;
	private Map<String, ArenaInterface> arena;
	
	
	public MainCommand(GameArena gameArena){
		this.plugin		= gameArena;
		this.arena		= new HashMap<String, ArenaInterface>();
		this.plugin.getLog().info("MainCommand ready");
	}
	
	/**
	 * Retourne l'objet Arene de l'areneName
	 * @param playerName
	 * @return
	 */
	private ArenaInterface Arena(String playerName){
		return this.arena.get( playerName );
	}
	
	/**
	 * Affiche l'aide exprese pour l'utilisation
	 * Pas fini, du tout ^^
	 * @param player
	 */
	private void usage(Player player) {
		player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena list" + ChatColor.RESET + " for list all arena available.");
		player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena join <arena name>" + ChatColor.RESET + " to join arena");
		player.sendMessage(this.plugin.getPrefix() + ChatColor.GREEN + "=========="+ ChatColor.GOLD + " Admin " + ChatColor.GREEN +"==========");
		if (player.hasPermission("gamearena.creator") || !player.isOp()){
			player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena create <arena name>" + ChatColor.RESET + " for create arena");
			player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena load <arena name>" + ChatColor.RESET + " for load arena");
			player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena mode list" + ChatColor.RESET + " for list all mode available");
			player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena mode <mode name>" + ChatColor.RESET + " for select mode");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("arena")){
			return false;
		}
		if (!(sender instanceof Player)){
			this.plugin.getLog().info("You are not a player");
			return false;
		}
		Player player = (Player) sender;
		
		player.sendMessage(this.plugin.getPrefix() + ChatColor.GREEN + "=========="+ ChatColor.GOLD + " Arena " + ChatColor.GREEN +"==========");
		
		if (args.length < 1){
			this.plugin.getLog().info("Argument too small");
			this.usage(player);
			return true;
		}

		/**
		 * Just inDev
		 */
		if (args[0].equalsIgnoreCase("testing")){
			player.sendMessage("testing return:");
			String arenaName = this.plugin.getPlayerArena(player.getName().toString());
			if (arenaName == null){
				player.sendMessage("arenaName == null");
			} else {
				player.sendMessage("arenaName == " + arenaName);
				ArenaInterface arena = this.plugin.getArena( arenaName );
				if (arena instanceof ArenaInterface){
					player.sendMessage("arena instanceof ArenaInterface");
					player.sendMessage("TDM");
					TeamDeathMatch tdm = (TeamDeathMatch) arena.getGame();
					
					if (tdm == null){
						player.sendMessage("tdm == null");
					} else {
					
						for (String teamName : tdm.getTeamManager().getTeamList()){
							player.sendMessage("TeamName == " + teamName);
							Team team = tdm.getTeamManager().getTeam(teamName);
							for (String playerName : team.getPlayerList().keySet()){
								Player currentPlayer = Bukkit.getPlayer(playerName);
								if (currentPlayer == null){
									player.sendMessage("playerName == " + playerName + " Bukkit return null");
								} else {
									String o = (currentPlayer.isOnline()) ? "y" : "n";
									player.sendMessage("playerName == " + playerName + " online:" + o);
									if (currentPlayer.isOnline()){
										player.sendMessage("teleport test");	
										currentPlayer.teleport( tdm.getTeamSpawn(teamName) );
										
									}
								}
								
							}
						}
						
					}
				}
				
			}
			return true;
		}
		
		
		/**
		 * Test des permissions pour le mode création
		 */
		if (
				(
					args[0].equalsIgnoreCase("create")	||	args[0].equalsIgnoreCase("load") || 
					args[0].equalsIgnoreCase("set")		||	args[0].equalsIgnoreCase("get") ||
					args[0].equalsIgnoreCase("mode") 
				)
					&& 
				(
					!player.hasPermission("gamearena.creator") || !player.isOp()
				)
			){
			player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + "You dont have permission \"gamearena.creator\"");
			return true;
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
			this.plugin.getLog().info("Argument too small set get mode");
			this.usage(player);
			return true;
		}
		
		
		/**
		 * Tout est tésté sur la logique, maintenant le traitement
		 */
		switch(args[0].toString()){
			case "leave":
				if (this.plugin.isPlayerInArena(player.getName().toString())){
					this.plugin.removePlayerFromArena(player.getName().toString());
					
					// on la charge et lance la requête
					ArenaInterface curArena = this.plugin.getArena(this.plugin.getPlayerArena( player.getName().toString() ));
					if (curArena == null){
						this.plugin.getLog().info("leave but no arena found");
					} else {
						curArena.leave(player);
						player.sendMessage("Experimental exit arena");
					}
					
				}
			break;
			case "join":
				if (args.length < 2 || !this.plugin.isArena(args[1].toString())){
					// Oui mais non, le header de command est envoyé avant...
					// player.performCommand("arena list");
					player.sendMessage(this.plugin.getPrefix() + "Unknown arena");
					player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena list" + ChatColor.RESET + " for list all arena available.");
					return true;
				}
				
				// l'arene est disponible ?
				if (!this.plugin.isArena( args[1].toString() )){
					player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + "Unknow arena");
					player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena list" + ChatColor.RESET + " for list all arena available.");
					return true;
				}
				
				// on la charge et lance la requête
				ArenaInterface curArena = this.plugin.getArena(args[1].toString());
				try {
					// Envois la requête pour rejoindre
					if (curArena.join(player, args)){
						this.plugin.getLog().info("MainCommand join " + args[1].toString() + " success for " + player.getName().toString());
					}
					// Si pas... Techniquement l'arene renvois une exception, avec la raison
				} catch (Exception e) {
					e.printStackTrace();
					player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + e.getMessage() + " ici");
				}
			break;
			
			case "list":
				if (this.plugin.getArenaList().size() == 0){
					player.sendMessage(this.plugin.getPrefix() + "Never arena found");
					return true;
				}
			
				for(String game : this.plugin.getArenaList()){
					// Charge le jeu game
					ArenaInterface ar = this.plugin.getArena(game);
					if (ar != null){
						String[] name = { "get", "name" };
						String[] mode = { "get", "mode" };
						try {
							player.sendMessage(
								this.plugin.getPrefix() + 
								ChatColor.GOLD + 
								ar.get(name, player) 
								+ " " + 
								ChatColor.RESET + 
								MatchMode.get( ar.get(mode, player) ).toString() 
								
								// TODO un retour particulier style TDM 3/16 ou le score actuelle
							);
						} catch (Exception e) {
							e.printStackTrace();
							this.plugin.getLog().severe( "Exception on read list of arena: " + e.getMessage() );
						}
					}
				}
			break;
			
			/**
			 * Custom & creation
			 */

			case "create":
				this.arena.put(player.getName().toString(), new ArenaMaster(this.plugin, args[1].toLowerCase(), player));
			break;
			case "load":
				this.arena.put(player.getName().toString(), new ArenaMaster(this.plugin, args[1].toLowerCase(), player));
			break;
			
			case "set":
				/**
				 * Rien a faire ici, juste l'envois de la requête
				 */
				ArenaInterface set = this.Arena( player.getName().toString() );
				try {
					set.set(args, player);
				} catch (Exception e) {
					e.printStackTrace();
					player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + e.getMessage());
				}
			break;
			
			case "get":
				/**
				 * Rien a faire ici, juste l'envois de la requête
				 */
				ArenaInterface get = this.Arena( player.getName().toString() );
				try {
					get.get(args, player);
				} catch (Exception e) {
					e.printStackTrace();
					player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + e.getMessage());
				}
			break;
			
			case "mode":
				if (args[1].equalsIgnoreCase("list")){

					for (MatchMode mode : MatchMode.values()) {
						if (mode.isEnabled()){
							player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + mode.getTag() + ChatColor.RESET + " for " + ChatColor.GOLD + mode.toString());
						}
					}
					
					return true;
				}
				
				if (!MatchMode.contains( args[1].toString() )){
					player.sendMessage(this.plugin.getPrefix() + "Game mode not found");
					return true;
				}
				
				ArenaInterface megame = this.Arena( player.getName().toString() );
				megame.setMode( args[1].toString() );
				try {
					megame.sendUsage(player);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			break;
			
			case "test":
				if ( !this.arena.containsKey( player.getName().toString() ) ){
					this.plugin.getLog().info("has test, but not loaded");
					this.usage(player);
					return false;
				}
				
				ArenaInterface test = this.Arena( player.getName().toString() );
				try {
					this.plugin.getLog().info("Call testing for " + player.getName().toString());
					if (test.testing()){
						player.sendMessage(this.plugin.getPrefix() + ChatColor.GREEN + "Arena ready");
					} else {
						player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + "Arena not ready");
					}
				} catch (Exception e) {
					e.printStackTrace();
					player.sendMessage(this.plugin.getPrefix() + ChatColor.RED + e.getMessage());
				}
			break;
			
			default:
				this.plugin.getLog().info("Oh shit is default");
				this.usage(player);	
			break;
		}

		return false;
	}

}
