package me.devphp.ga.games.tdm;

import java.util.HashMap;

import me.devphp.teams.TeamManager;

public class ScoreTeams {
	private HashMap<String, Integer> teamPoint;
	private TeamManager teams;
	
	public ScoreTeams(){
		this.teamPoint = new HashMap<String, Integer>();
	}
	
	private boolean isTeam(String teamName){
		return (this.teamPoint.containsKey(teamName));		
	}
	
	public void createTeam(String teamName){
		if (!this.isTeam(teamName)){
			this.teamPoint.put(teamName, 0);
		}
	}
	
	public Integer getTeamPoint(String teamName){
		if (this.isTeam(teamName)){
			return this.teamPoint.get(teamName);
		}
		return 0;
	}

	public void addTeamPoint(String teamName, Integer point){
		if (this.isTeam(teamName)){
			Integer pt = this.getTeamPoint(teamName);
			pt = pt+point;
			this.teamPoint.put(teamName, pt);
		}
	}
	
	public void incrementTeamPoint(String teamName){
		if (this.isTeam(teamName)){
			Integer pt = this.getTeamPoint(teamName);
			pt++;
			this.teamPoint.put(teamName, pt);
		}
	}
	
}
