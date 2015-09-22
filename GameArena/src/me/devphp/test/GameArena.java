package me.devphp.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.devphp.iPlugin;
import me.devphp.PLog;
import me.devphp.test.Arena.ArenaInterface;
import me.devphp.test.Arena.ArenaMaster;


public class GameArena extends JavaPlugin implements iPlugin {
	
	private File configFile;
	private YamlConfiguration config;
	private String prefixNoColor	= "[GameArena] ";
	private String prefix			= ChatColor.RESET + ""+ ChatColor.GRAY + "[" + ChatColor.GREEN + "GameArena" + ChatColor.GRAY + "] " + ChatColor.RESET;

	public HashMap<String, ArenaInterface> arenaList;
	private Map<String, String> playerArena;
	private PLog plog;
	
	public void onEnable() {
		this.configFile			 	= new File("plugins/GameArena/config.yml");
		this.config					= YamlConfiguration.loadConfiguration(this.configFile);
		// Liste des arenes
		this.arenaList				= new HashMap<String, ArenaInterface>();
		this.playerArena			= new HashMap<String, String>();
		this.plog					= new PLog(this.getPrefixNoColor(), false);
		
		/**
		 * MainCommand ce charge des commandes de base pour les arenes
		 */
		this
			.getCommand("arena")
			.setExecutor(new MainCommand(this));
		
		/**
		 * MainListener va recevoir les events et envoyer à son tour a ArenaEventListener
		 * qui s'occupe alors d'informer les class qui l'implements
		 */
		this
			.getServer()
			.getPluginManager()
			.registerEvents(new MainListener(this), this);	
		
		// Si il y a une configuration
		if (this.config.contains("arena")){
			// On récupère la liste des arenes
			Set<String> arenaList		= this.config.getConfigurationSection("arena").getKeys(false);		
			for (String arenaName : arenaList){
				// Charge l'Arene en mode jeu 
				this.getLog().info("GameArena: Create " + arenaName);
				this.arenaList.put(arenaName, new ArenaMaster(this, arenaName));
			}
		}
	}
	
	public boolean isArena(String arena){
		return (this.arenaList.containsKey(arena));
	}
	
	public ArenaInterface getArena(String arena){
		return this.arenaList.get(arena);
	}
	

	
	public Set<String> getArenaList(){
		return this.arenaList.keySet();
	}
	
	public boolean isPlayerInArena(String playerName){
		return this.playerArena.containsKey(playerName);
	}
	
	public void removePlayerFromArena(String player){
		this.getLog().info("Remove " + player + " from playerArena");
		this.playerArena.remove(player);
	}
	
	public void setPlayerArena(String playerName, String arenaName){
		this.getLog().info("Add " + playerName + " to playerArena " + arenaName);
		this.playerArena.put(playerName, arenaName);
	}
	
	public String getPlayerArena(String playerName){
		return this.playerArena.get(playerName);
	}

	public PLog getLog(){
		return this.plog;
	}
	
	@Override
	public JavaPlugin getPlugin() {
		return this;
	}
	
	@Override
	public String getPrefix() {
		return this.prefix;
	}

	@Override
	public String getPrefixNoColor() {
		// TODO Auto-generated method stub
		return this.prefixNoColor;
	}

	@Override
	public YamlConfiguration getConfig() {
		return this.config;
	}

	@Override
	public File getConfigFile() {
		return this.configFile;
	}

}
