package me.devphp.test.Arena.MatchMode.tdm;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.devphp.player.PlayerData;
import me.devphp.player.PlayerStat;
import me.devphp.player.ScoreTeams;
import me.devphp.teams.Team;
import me.devphp.teams.TeamManager;
import me.devphp.test.GameArena;
import me.devphp.test.Arena.ArenaEventInterface;
import me.devphp.test.Arena.ArenaInterface;

public class TeamDeathMatch implements ArenaInterface {
	private String chatHeader = ChatColor.GREEN + "===== " + ChatColor.GOLD + "Team Death Match" + ChatColor.GREEN + " =====";
	
	private GameArena plugin;
	private YamlConfiguration config;
	private String arena;
	
	private boolean hasReady;
	
	public ArenaInterface game = null;
	private ArenaEventInterface event;
	private TeamDeathMatchTimer timer;
	private TeamManager teamManager;
	
	private int gametime;
	private Integer teamMinSize = 1;
	private Integer teamMaxSize = 4;

	private HashMap<String, Location> teamSpawn;
	
	private ScoreTeams scoreTeams;
	private HashMap<String, PlayerData> playerData;
	private HashMap<String, PlayerStat> playerStat;
	
	

	public TeamDeathMatch(GameArena plugin, String arena) {
		this.plugin			= plugin;
		this.config			= this.plugin.getConfig();
		this.arena			= arena;
		this.event			= new TeamDeathMatchEvent(this);
		this.hasReady		= true;
		this.scoreTeams		= new ScoreTeams();
		this.teamSpawn		= new HashMap<String, Location>();
		
		init();
	}
	
	private void init(){
		this.teamManager	= new TeamManager(this.plugin);
		this.playerData		= new HashMap<String, PlayerData>();
		this.playerStat		= new HashMap<String, PlayerStat>();
		
		this.scoreTeams.reset();		
		
		if (this.config.contains("arena." + this.arena + ".team")){
			
			for (String teamName : this.config.getConfigurationSection("arena." + this.arena + ".team").getKeys(false)){
				// Définie le spawn de l'équipe
				this.teamSpawn.put(teamName, new Location(
						Bukkit.getWorld( this.config.getString("arena." + this.arena + ".team." + teamName + ".w") ),
						this.config.getDouble("arena." + this.arena + ".team." + teamName + ".x"),
						this.config.getDouble("arena." + this.arena + ".team." + teamName + ".y"),
						this.config.getDouble("arena." + this.arena + ".team." + teamName + ".z")
					));
				this.getTeamManager().createTeam(teamName);
				this.getScoreTeams().createTeam(teamName);
			}
			
			if (this.config.contains("arena." + this.arena + ".p2w")){
				((TeamDeathMatchEvent) this.event).setPoint2wins(this.config.getInt("arena." + this.arena + ".p2w"));
			}
			
			if (this.config.contains("arena." + this.arena + ".gametime")){
				this.gametime = this.config.getInt("arena." + this.arena + ".gametime");
			}
		}
	}
	
	public GameArena getPlugin(){
		return this.plugin;
	}
	
	public ScoreTeams getScoreTeams(){
		return this.scoreTeams;
	}
	
	public PlayerStat getPlayerStat(String playerName){
		return this.playerStat.get(playerName);
	}
	
	public TeamManager getTeamManager(){
		return this.teamManager;
	}
	
	public Integer getTeamMinSize(){
		return this.teamMinSize;
	}
	
	public Integer getTeamMaxSize(){
		return this.teamMaxSize;
	}
	
	public Integer getGametime(){
		return this.gametime;
	}
	
	public String getArenaName(){
		return this.arena;
	}

	public Location getTeamSpawn(String teamName) {
		return this.teamSpawn.get(teamName);
	}
	
	private boolean checkTeamsSize(){
		this.plugin.getLog().info("TeamDeathMatch.checkTeamsSize" );
		this.plugin.getLog().info("TDM " + this.arena + " nb team = " + this.getTeamManager().getTeamCount() );
		
		if (this.getTeamManager().getTeamCount() > 1){
			for (String teamName : this.getTeamManager().getTeamList()){
				
				Team team = this.getTeamManager().getTeam(teamName);
				if (team == null){
					this.plugin.getLog().severe("TDM " + this.arena + " team = " + teamName + ": return Team == null");
				}
				
				this.plugin.getLog().info("TeamDeathMatch.checkTeamsSize " + team.getTeamCount() + " < " + this.getTeamMinSize());
				if (team.getTeamCount() < this.getTeamMinSize()){
					return false;
				}
			}
			
			return true;
		} else {
			this.plugin.getLog().severe("TDM " + this.arena + " require minimum two teams");
		}
		return false;
	}
	
	public boolean startGame(){
		// Temps en seconde
		this.getTeamManager().broadcast(this.chatHeader);
		this.getTeamManager().broadcast("Team Death Match ready. Teleport player on spawn");

		/**
		 * Parcourt la liste des équipes
		 */
		for (String teamName : this.getTeamManager().getTeamList()){
			this.getPlugin().getLog().info("TeamName == " + teamName);
			Team team = this.getTeamManager().getTeam(teamName);
			
			Map<String, Player> playerList = team.getPlayerList();
			for (String playerName : playerList.keySet()){
				if (playerList.containsKey(playerName)){
					Player currentPlayer = playerList.get(playerName);
					if (currentPlayer == null){
						this.getPlugin().getLog().info("playerName == " + playerName + " Bukkit return null");
					} else {
						String o = (currentPlayer.isOnline()) ? "y" : "n";
						this.getPlugin().getLog().info("playerName == " + playerName + " online:" + o);
						if (currentPlayer.isOnline()){
							this.getPlugin().getLog().info("teleport to spawn");
							this.playerData.put(currentPlayer.getName().toString(), new PlayerData(currentPlayer));
							this.playerStat.put(currentPlayer.getName().toString(), new PlayerStat());
							
							this.clearPlayerStat(currentPlayer);
							currentPlayer.teleport( this.getTeamSpawn(teamName) );
						}
					}
				}
			}
		}
				
		if (this.config.contains("arena." + this.arena + ".gametime")){
			this.timer = new TeamDeathMatchTimer(this, this.config.getInt("arena." + this.arena + ".gametime"));
			this.timer.runTaskTimer(this.plugin, /*delay*/ 10, /*period*/20);
		}
		
		/*try {
			// TODO Remove his, is just for test
			Thread.sleep(10000);
			this.endGame();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//*/
		
		return true;
	}
	
	
	public void endGame(){		
		this.getTeamManager().broadcast(this.chatHeader);
		this.getTeamManager().broadcast("Congratulation. The game is now finish. Thanks for participation.");
		this.broadcastScore();

		for (String teamName : this.getTeamManager().getTeamList()){
			this.getPlugin().getLog().info("TeamDeathMatch.endGame: teamName == " + teamName);
			Team team = this.getTeamManager().getTeam(teamName);
			if (team == null){
				this.getPlugin().getLog().info("TeamDeathMatch.endGame: team == null");
			} else {
				Map<String, Player> playerList = team.getPlayerList();
				this.getPlugin().getLog().info("TeamDeathMatch.endGame: playerList = " + playerList.size());
				if (playerList.size() > 0) {
					


					for (String playerName : playerList.keySet()){
						if (playerList.containsKey(playerName)){
							Player player = playerList.get(playerName);
							if (player.isOnline()){
								
								if (player.isOnline()){
									// this.playerData.get(pl.getName().toString()).restore();
									PlayerStat ps = this.playerStat.get(player.getName().toString());
									if (ps != null){
										int kill = ps.getKill();
												   ps.setKill(0);
										int death = ps.getDeath();
													ps.setDeath(0);
										int streak = ps.getStreak();
													 ps.setStreak(0);
										double rate = (kill / (kill+death+1)*1); 
										
										String stat = "You made " + kill + " victims , your best series is " + streak + ", you're dead " + death + " times. Your ratio is " + String.format("%.2f", rate);
										player.sendMessage(stat);
									}
									
									PlayerData pd = this.playerData.get(player.getName().toString());
									if (pd != null){
										pd.restore();
										this.playerData.remove(player.getName().toString());
									}
									this.leave(player);
								}
				                
				            }
						}
					}

				}
			}
		}
		

		this.init();
	}
	
	public void broadcastScore(){
		Map<Integer, String> unsortMap = new HashMap<Integer, String>();
		for (String teamName : this.getTeamManager().getTeamList()){
			unsortMap.put(this.scoreTeams.getTeamPoint(teamName), teamName);
		}
		
		Map<Integer, String> treeMap = new TreeMap<Integer, String>(
			new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o2.compareTo(o1);
				}
			}
		);
		treeMap.putAll(unsortMap);
		
		int i = 0;
		for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
			i++;
			this.getTeamManager().broadcast(i + ": " + entry.getValue() + " " + entry.getKey() + " pts");
		}
	}
	
	
	private void clearPlayerStat(Player player){
		/**
		 * Remove all potion effect
		 */
		for (PotionEffect effect : player.getActivePotionEffects()){
	        player.removePotionEffect(effect.getType());
		}

		player.getWorld().setPVP(true);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFlying(false);
		player.setFoodLevel(20);
		player.setHealth(20.00);
		player.setLevel(0);
	}
	

	@Override
	public boolean hasReady() {
		return this.hasReady;
	}

	@Override
	public boolean setMode(String mode) {
		// Never here, is ArenaMaster
		return false;
	}

	@Override
	public boolean join(Player player, String[] args) {
		
		if (!(this.getTeamManager() instanceof TeamManager)){
			this.getPlugin().getLog().info("TeamManager n'est pas TeamManager ");
			return false;
		}
		

		
		int nb = -1;
		String teamName = null;
		
		for (String key : this.getTeamManager().getTeamList()){
			if (teamName == null){
				teamName = key;
				nb = this.getTeamManager().getTeam(key).getPlayerList().size();
			}

			if (!(this.getTeamManager().getTeam(key) instanceof Team)){
				this.getPlugin().getLog().info("Team n'est pas Team ");
				return false;
			}
			
			if (this.getTeamManager().getTeam(key).getPlayerList().size() < nb){
				teamName = key;
				nb = this.getTeamManager().getTeam(key).getPlayerList().size();
			}
		}
		
		this.getTeamManager().joinTeam(player.getName().toString(), teamName);
		
		if (this.checkTeamsSize()){
			this.startGame();
		}
		
		// TODO Check si la team est full ?
		return true;
	}

	@Override
	public void leave(Player player) {
		this.getPlugin().getLog().info("TeamDeathMatch.leave");
		
		try {
			String playerName = player.getName().toString();
			if (this.getTeamManager().isPlayerInTeam( playerName )){
				String teamName = this.getTeamManager().getPlayerTeam( playerName );
				Team team = this.getTeamManager().getTeam(teamName);
				if (team == null){
					this.getPlugin().getLog().info("TeamDeathMatch.leave: team == null");
				} else {
					this.getPlugin().getLog().info("TeamDeathMatch.leave: player > " + playerName);
					team.removeFromTeam(playerName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.plugin.removePlayerFromArena(player.getName().toString());
	}

	@Override
	public String get(String[] args, Player player) throws Exception {
		if (args.length < 2){
			return null;
		}
		
		switch(args[1].toString()){
			case "teams":
				for (String teamName : this.getTeamManager().getTeamList()){
					
					player.sendMessage( teamName + " contains:" );
					for(String playerName : this.getTeamManager().getTeam(teamName).getPlayerList().keySet()){
						player.sendMessage( playerName );
					}
					
					
				}

			break;
		}
		return null;
	}

	@Override
	public void set(String[] args, Player player) throws Exception {

		if (args.length == 3){
			
			switch(args[1].toString()){
				case "team":
					this.config.set("arena." + this.arena + ".team." + args[2].toString() + ".w", player.getLocation().getWorld().getName().toString());
					this.config.set("arena." + this.arena + ".team." + args[2].toString() + ".x", player.getLocation().getX());
					this.config.set("arena." + this.arena + ".team." + args[2].toString() + ".y", player.getLocation().getY());
					this.config.set("arena." + this.arena + ".team." + args[2].toString() + ".z", player.getLocation().getZ());

					player.sendMessage(this.plugin.getPrefix() + this.chatHeader);
					player.sendMessage(this.plugin.getPrefix() + "Team created and spawn defined");
				break;
				case "p2w":
					if (Integer.valueOf(args[2].toString()) <= 0){
						throw new Exception("Oops argument is not a number or is equal 0");
					}
					
					this.config.set("arena." + this.arena + ".p2w", Integer.valueOf(args[2].toString()));
					
					player.sendMessage(this.plugin.getPrefix() + this.chatHeader);
					player.sendMessage(this.plugin.getPrefix() + "Point to wins defined");
				break;
				case "gametime":
					if (Integer.valueOf(args[2].toString()) <= 0){
						throw new Exception("Oops argument is not a number or is equal 0");
					}
					
					this.config.set("arena." + this.arena + ".gametime", Integer.valueOf(args[2].toString()));
					
					player.sendMessage(this.plugin.getPrefix() + this.chatHeader);
					player.sendMessage(this.plugin.getPrefix() + "Game time defined");
				break;
				default:
					this.sendUsage(player);
				break;
			}
		}
	}

	@Override
	public boolean testing() throws Exception {
		this.plugin.getLog().info("TeamDeathMatch.testing() called");

		if (!this.config.contains("arena." + this.arena + ".team")){
			throw new Exception("Use " + ChatColor.GOLD + "/arena set team <team name>" + ChatColor.RED + " define team name");
		}
		
		Set<String> key = this.config.getConfigurationSection("arena." + this.arena + ".team").getKeys(false);
		if (key.size() == 1){
			throw new Exception("Use " + ChatColor.GOLD + "/arena set team <team name>" + ChatColor.RED + " define team name");
		}
		
		if (!this.config.contains("arena." + this.arena + ".p2w")){
			throw new Exception("Use " + ChatColor.GOLD + "/arena set p2w <number of point>" + ChatColor.RED + " set point needed to wins games");
		}
		
		if (!this.config.contains("arena." + this.arena + ".gametime")){
			throw new Exception("Use " + ChatColor.GOLD + "/arena set gametime <time in second>" + ChatColor.RED + " set timeout of the game");
		}
		
		try {
			this.config.save(this.plugin.getConfigFile());
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("Save configuration file fail");
		}
		
		return true;
	}

	@Override
	public void sendUsage(Player player) throws Exception {
		player.sendMessage(this.plugin.getPrefix() + ChatColor.GREEN + "===== " + ChatColor.GOLD + "Team Death Match" + ChatColor.GREEN + " =====");
		player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena set team <team name>" + ChatColor.RESET + " define team name");
//		player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena set spawn <team name>" + ChatColor.RESET + " set spawn location (on your position)");
		player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena set p2w <number of point>" + ChatColor.RESET + " set point needed to wins games");
		player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena set gametime <seconds>" + ChatColor.RESET + " set maximum game time");
		player.sendMessage(this.plugin.getPrefix() + "Finish the arena with " + ChatColor.GOLD + "/arena test");
		player.sendMessage(this.plugin.getPrefix() + ChatColor.GOLD + "Happy new game");
	}

	@Override
	public void threadRunningGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArenaEventInterface getEvent() {
		return this.event;
	}

	@Override
	public ArenaInterface getGame() {
		return this;
	}

}
