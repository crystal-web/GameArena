package me.devphp;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Interface pour tous plugin afin de résoudre les problème de compatibilités
 * @author Devphp
 *
 */
public interface iPlugin {
	public String getPrefix();
	public String getPrefixNoColor();
	public JavaPlugin getPlugin();
	public YamlConfiguration getConfig();
	public File getConfigFile();
	public PLog getLog();
}
