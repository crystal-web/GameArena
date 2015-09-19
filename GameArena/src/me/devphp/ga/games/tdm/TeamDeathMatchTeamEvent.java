package me.devphp.ga.games.tdm;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.devphp.player.PlayerStat;
import me.devphp.teams.TeamEvent;



public class TeamDeathMatchTeamEvent implements TeamEvent {
	private TeamDeathMatch tdm;
	private int p2w;
	
	private Map<String, PlayerStat> ps = new HashMap<String, PlayerStat>();
	public TeamDeathMatchTeamEvent(TeamDeathMatch tdm){
		this.tdm = tdm;
	}
	
	public void setPoint2wins(Integer p2w){
		this.p2w = p2w;
	}
	
	private String replaceAll(String input, CharSequence replace, CharSequence... charseq) {
		CharSequence[] arrayOfCharSequence;
		int j = (arrayOfCharSequence = charseq).length;
		for (int i = 0; i < j; i++) {
			CharSequence chars = arrayOfCharSequence[i];
			input = input.replace(chars, replace);
		}
		return input;
	}
	
	// TODO test
	private String[] tags = { "kills", "deaths", "streak" };
	// TODO test
	private void setScoreboard(Player player){
		// TODO la scoreboard ne devrai pas etre dans ScoreTeams ?
		Scoreboard b = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective ob = b.registerNewObjective("TeamDeathMatch", "dummy");
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		String stats = "stats"; // Utils.format(getTag("stats"));
		stats = replaceAll(stats, player.getName(), new CharSequence[] { "{PLAYER}", "{NAME}", "%PLAYER%", "%NAME%" });
		ob.setDisplayName(stats);

		ob.getScore("Kills").setScore(0);
		ob.getScore("Deaths").setScore(0);
		ob.getScore("Streak").setScore(0);
		player.setScoreboard(b);
	}

	// TODO test
	private void updateScoreboard(Player player){
		Scoreboard b = player.getScoreboard();
		if ((b == null) || (b.getObjective("TeamDeathMatch") == null)) {
			this.setScoreboard(player);
		}
	
		
		Objective ob = b.getObjective("TeamDeathMatch");
		ob.getScore("Kills").setScore(ps.get(player.getName().toString()).getKill());
		ob.getScore("Deaths").setScore(ps.get(player.getName().toString()).getDeath());
		ob.getScore("Streak").setScore(ps.get(player.getName().toString()).getStreak());
	}
	
	
	@Override
	public void teamCreatedEvent(String teamName) {
		this.tdm.scoreTeams.createTeam(teamName);
	}

	@Override
	public void teamJoinEvent(String teamName, Player player) {
		setScoreboard(player);
		ps.put(player.getName().toString(), new PlayerStat());
		
		// test le nombre de joueur est ok 
		int min = this.tdm.tm.getMinTeamSize();
		
		for( String team : this.tdm.tm.getTeams()){
			if (this.tdm.tm.getTeam(team).getTeamCount() < min){
				return;
			}
		}

		this.tdm.startGame();
	}

	@Override
	public void teamKillEvent(String teamName, String playerName, PlayerDeathEvent event) {		
		if (event.getEntity().getKiller() instanceof Player){
			Player killer = event.getEntity().getKiller();
			killer.getWorld().playSound(killer.getLocation(), Sound.FALL_BIG,1, 0);
			// Si le mort est un joueur (il doit aussi etre en jeu)
			if (event.getEntity() instanceof Player){
				Player death = event.getEntity();
				// Ici on évite aussi les nullpointerexception ^^ 
				if (this.tdm.tm.isPlayerInTeam(death.getName().toString())){
					ps.get(killer.getName().toString()).kill();
					updateScoreboard(killer);
					
					// Ajout des points a l'equipe
					this.tdm.scoreTeams.incrementTeamPoint(teamName);
					
					this.tdm.broadcastMessage("[" + ChatColor.GOLD + teamName + ChatColor.RESET + "]"
							+ killer.getName().toString() + " has killed [" + ChatColor.GOLD
							+ this.tdm.tm.getPlayerTeamName(death.getName().toString()) + ChatColor.RESET + "]"
							+ death.getName().toString());

					// Testy si le score permet l'arret de jeu
					if (this.tdm.scoreTeams.getTeamPoint(teamName) >= this.p2w){
						this.tdm.endGame();
					}
				}
			}
		
		}
	}

	@Override
	public void teamRespawnEvent(String teamName, String playerName, PlayerRespawnEvent event) {
		final Player player = event.getPlayer();		
		final Location teamSpawn = new Location(
				Bukkit.getWorld(this.tdm.config.getString("arena." + this.tdm.arena + ".team." + teamName + ".w")), 
				this.tdm.config.getDouble("arena." + this.tdm.arena + ".team." + teamName + ".x"),
				this.tdm.config.getDouble("arena." + this.tdm.arena + ".team." + teamName + ".y"),
				this.tdm.config.getDouble("arena." + this.tdm.arena + ".team." + teamName + ".z")
			);
		
		new BukkitRunnable() {
			  public void run() {
				  player.teleport(teamSpawn);
			  }
			}.runTaskLater(this.tdm.plugin, 20L);
	}


	
	@Override
	public void teamLeaveEvent(String teamName, String playerName) {
		// TODO Auto-generated method stub
	}

	@Override
	public void teamDeathEvent(String teamName, String playerName, PlayerDeathEvent event) {
		Player player = event.getEntity();
		ps.get(player.getName().toString()).death();
		updateScoreboard(player);
	}
}
