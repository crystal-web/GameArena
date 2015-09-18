package me.devphp.ga.games.tdm;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Usage: BukkitTask test = new TeamDeathMatchTimer(this, 5).runTaskTimer(this, 10, 20);
 * @author Devphp
 *
 */
public class TeamDeathMatchTimer extends BukkitRunnable {
	 
    private final JavaPlugin plugin;
    private TeamDeathMatch tdm;
    private int counter;
 
    public TeamDeathMatchTimer(JavaPlugin plugin, TeamDeathMatch tdm, int counter) {
        this.plugin = plugin;
        this.tdm = tdm;
        
        if (counter < 1) {
            throw new IllegalArgumentException("counter must be greater than 1");
        } else {
            this.counter = counter;
        }
    }
 
    @Override
    public void run() {
    	counter--;
    	if (counter < 30 && counter > 0){
    		this.tdm.broadcastMessage("It remains " + counter + " seconds");
    	} else if (counter <= 0){
            this.cancel();
            this.tdm.endGame();
            this.tdm.reset();
        }
    }
 
}