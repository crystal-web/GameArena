package me.devphp.test.Arena.MatchMode.tdm;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Usage: BukkitTask test = new TeamDeathMatchTimer(this, 5).runTaskTimer(this, 10, 20);
 * @author Devphp
 *
 */
public class TeamDeathMatchTimer extends BukkitRunnable {
    private TeamDeathMatch tdm;
    private int counter;
 
    public TeamDeathMatchTimer(TeamDeathMatch tdm, int counter) {
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
	
    	if (counter == 30){
    		this.tdm.getTeamManager().broadcast("End of game in 30 seconds");
    	} else if (counter == 25){
    		this.tdm.getTeamManager().broadcast("End of game in 25 seconds");
    	}  else if (counter == 20){
    		this.tdm.getTeamManager().broadcast("End of game in 20 seconds");
    	} else if (counter == 15){
    		this.tdm.getTeamManager().broadcast("End of game in 15 seconds");
    	}  else if (counter <= 10 && counter > 0){
    		this.tdm.getTeamManager().broadcast("End of game in " + counter + " seconds");
    	} else if (counter <= 0){
            this.cancel();
            this.tdm.endGame();
        }
    	
    }
 
}