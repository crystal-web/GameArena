package me.devphp.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Team {
	private String teamName;
	public Logger log = Logger.getLogger("Minecraft");
	private Map<String, Player> joinedPlayer;
	
	public Team(String teamName)
    {
        this.teamName = teamName;
        this.joinedPlayer = new HashMap<String, Player>();
    }
	
	public String getTeamName() {
		return this.teamName;
	}
	
    public int getTeamCount()
    {
    	return this.joinedPlayer.size();
    }
    
    public Map<String, Player> getPlayerList()
    {
    	return this.joinedPlayer;
    }
        
    public void addToTeam(Player player)
    {
        if(player == null){
        	log.severe("[me.devphp.teams] args player is null");
        	return;
        }
        
        if(!isInTeam(player.getName().toString())){
        	sendTeamMessage(player + " has joined the team.");
            joinedPlayer.put(player.getName().toString(), player);
        }
    }

	public void removeFromTeam(String playerName)
    {
		if (this.joinedPlayer.containsKey(playerName)){
			this.joinedPlayer.remove(playerName);
			this.sendTeamMessage(ChatColor.GOLD + playerName + ChatColor.RESET + " has left the team.");
		}
    }
	
    public void removeAllFromTeam()
    {
        joinedPlayer.clear();
    }
    
    public boolean isInTeam(String playerName)
    {
    	return this.joinedPlayer.containsKey(playerName);
    }

	public void sendTeamChat(String message, String sendPlayer) {
		for (String playerName : this.joinedPlayer.keySet()){
			if (this.joinedPlayer.containsKey(playerName)){
				Player player = this.joinedPlayer.get(playerName);
				if (player.isOnline()){
	                player.sendMessage(ChatColor.GREEN + "[" + this.getTeamName() + "] " + sendPlayer + " " + ChatColor.RESET + message);
	            }
			}
		}
	}
    
    public void sendTeamMessage(String message) {
    	for (String playerName : this.joinedPlayer.keySet()){
			if (this.joinedPlayer.containsKey(playerName)){
				Player player = this.joinedPlayer.get(playerName);
				if (player.isOnline()){
					player.sendMessage(ChatColor.GREEN + "[" + this.getTeamName() + "] " + ChatColor.RESET + message);
	            }
			}
    	}
	}
}
