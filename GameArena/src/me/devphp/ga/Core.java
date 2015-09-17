package me.devphp.ga;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import me.devphp.iPlugin;
import test.ExampleSelfCancelingTask;


public class Core extends JavaPlugin implements iPlugin{
	public Logger log = Logger.getLogger("Minecraft");
	public String prefix = ChatColor.RESET + ""+ ChatColor.GRAY + "[" + ChatColor.GREEN + "GameArena" + ChatColor.GRAY + "] " + ChatColor.RESET;
	public String prefixNoColor = "[GameManager] ";
	public String errorColor = ChatColor.BOLD +""+ ChatColor.RED;
	
	public ArenaInterface game;
		
	// TODO Besoin de performance ? Collection ? ArrayList ?
	public Map<String, ArenaInterface> games;
	public File configFile;
	public YamlConfiguration config;
	
	/**
	 * Instancie les arenes défini dans le fichier de configuration
	 * @param core
	 * @return 
	 */
	public void onEnable() {
		this.configFile			 	= new File("plugins/GameArena/config.yml");
		this.config					= YamlConfiguration.loadConfiguration(this.configFile);
		this.games					= new HashMap<String, ArenaInterface>();
		
		if (this.config.contains("arena")){
			Set<String> arenaList		= this.config.getConfigurationSection("arena").getKeys(false);		
			for (String arenaName : arenaList){
				/**
				 * Charge l'Arene en mode jeu 
				 */
				this.games.put(arenaName, new ArenaGame(/* Core */ this, arenaName));
			}
		}
		
		PluginCommand commands = this.getCommand("arena");		
	
		if (commands != null) {
			commands.setExecutor(new ArenaCommand(this));
		} else {
			log.severe("Command Arena not loaded");
		}
		
		
	}
	
	@Override
	public YamlConfiguration getConfig(){
		return this.config;
	}
	
	@Override
	public File getConfigFile(){
		return this.configFile;
	}
	
	
	@Override
	public String getPrefix() {
		return this.prefix;
	}

	@Override
	public String getPrefixNoColor() {
		return this.prefixNoColor;
	}

	@Override
	public JavaPlugin getPlugin() {
		return this;
	}
	// */
}
