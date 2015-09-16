package me.devphp.teams;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class Team {
	private String teamName;
	private ArrayList<String> joinedPlayers;
	public Logger log = Logger.getLogger("Minecraft");
	
//	private TeamEvent event;
	
	public Team(String teamName)
    {
        this.teamName = teamName;
        this.joinedPlayers = new ArrayList<String>();
//        event.teamCreatedEvent(teamName);
    }

    public void addToTeam(String player)
    {
        if(player == null){
        	log.info("[me.devphp.team] args player is null");
        	return;
        }
        
        if(!isInTeam(player)){
            joinedPlayers.add(player);
            // event.teamJoinEvent(this.getTeamName(), player);
            sendTeamMessage(player + " has joined the team.");
        }
    }

	public void removeFromTeam(String playerName)
    {
        for(int i = 0; i < joinedPlayers.size(); i ++)
        {
            String thisPlayer = joinedPlayers.get(i);
            if(thisPlayer.equals(playerName))
            {
                joinedPlayers.remove(i);
                Player player = Bukkit.getPlayer(playerName);
                if(player != null && player.isOnline()) {
                    player.sendMessage(ChatColor.RED + "You are no longer in a team.");
                }
                
                this.sendTeamMessage(ChatColor.GOLD + thisPlayer + ChatColor.RESET + " has left the team.");
                return;
            }
        }
    }

    public boolean isInTeam(String playerName)
    {
        for(int i = 0; i < this.joinedPlayers.size(); i++)
        {
            String thisPlayer = this.joinedPlayers.get(i);
            if(thisPlayer.equals(playerName))
                return true;
        }
        return false;
    }
    
    public void sendTeamChat(String message, String srcPlayer)
    {    
    	this.sendTeamMessage(srcPlayer + ": " + message);
    }
    
    public void sendTeamMessage(String message) {
        for(int i = 0; i < this.joinedPlayers.size(); i ++)
        {
            Player player = Bukkit.getPlayer(this.joinedPlayers.get(i));
            if(player!= null && player.isOnline()){
                player.sendMessage(ChatColor.GREEN + "[" + this.getTeamName() + "] " + ChatColor.RESET + message);
            }
        }
	}
    
    
    public String[] getPlayerList()
    {
        String[] list = new String[joinedPlayers.size()];
        for(int i = 0; i < joinedPlayers.size(); i ++)
        {
            list[i] = joinedPlayers.get(i);
        }
        return list;
    }
    
    public int getTeamCount()
    {
        return this.joinedPlayers.size();
    }

	public String getTeamName() {
		return this.teamName;
	}
	
    public void removeAllFromTeam()
    {
        this.sendTeamMessage("Team is being disbanded...");
        joinedPlayers.clear();
    }

}
