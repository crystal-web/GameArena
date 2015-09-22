package me.devphp.GameArena.Arena;

import org.bukkit.entity.Player;


public interface ArenaInterface {
	public ArenaInterface game = null;

	public ArenaInterface getGame();
	
	public boolean hasReady();
	
	/**
	 * Set mode
	 * @param mode
	 * @return
	 */
	public boolean setMode(String mode);
	
	/**
	 * Rejoindre une arene, l'arene DOIT retourné TRUE si le joueur rejoind
	 * 
	 * @param player
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean join(Player player, String[] args) throws Exception;
	
	/**
	 * Quitter une arene
	 * @param player
	 */
	public void leave(Player player);
	
	/**
	 * Retourne la valeur correspondant a Argument
	 * @param args
	 * @return
	 */
	public String get(String[] args, Player player) throws Exception;
	
	/**
	 * Défini le parametre
	 * @param args
	 * @param player
	 * @throws Exception 
	 */
	public void set(String[] args, Player player) throws Exception;
		
	/**
	 * Test if every setting is ready for Arena
	 * @return
	 */
	public boolean testing() throws Exception;

	/**
	 * Renvois la methode d'utilisation du mode de jeu
	 * @param player
	 * @throws Exception
	 */
	public void sendUsage(Player player) throws Exception;

	/**
	 * Test pas utilisé actuellement
	 */
	public void threadRunningGame();
	
	public ArenaEventInterface getEvent();
}
