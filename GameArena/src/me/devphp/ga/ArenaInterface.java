package me.devphp.ga;

import org.bukkit.entity.Player;

import me.devphp.ga.games.Gamemode;

public interface ArenaInterface {
	public boolean hasReady();
	
	public void setMode(Gamemode mode);
	
	public boolean join(Player player, String[] args) throws Exception;
	
	/**
	 * Retourne la valeur correspondant a Argument
	 * @param args
	 * @return
	 */
	public String get(String[] args, Player player) throws Exception;
	
	/**
	 * Défini le parametre
	 * @param args
	 * @return
	 * @throws Exception 
	 */
	public boolean set(String[] args, Player player) throws Exception;
		
	/**
	 * Test if every setting is ready for Arena
	 * @return
	 */
	public boolean testing() throws Exception;

	public void sendUsage(Player player) throws Exception;

	public void threadRunningGame();
}
