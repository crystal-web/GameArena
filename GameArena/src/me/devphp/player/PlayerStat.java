package me.devphp.player;

public class PlayerStat {
	private int kill = 0;
	private int streak = 0;
	private int streakActual = 0;
	private int death = 0;
	
	public void setKill(int kill) {
		this.kill = kill;
	}
	
	public int getKill(){
		return this.kill;
	}
	
	public void kill(){
		this.kill = this.kill+1;
		streak();
	}
	
	public void setStreak(int kill) {
		this.streakActual = kill;
	}
	
	public int getStreak(){
		return this.streakActual;
	}
	
	public void streak(){
		this.streak = this.streak+1;
	}	

	public void setDeath(int death) {
		this.death = death;
	}
	
	public int getDeath(){
		return this.death;
	}
	
	public void death(){
		this.death = this.death+1;
		if (this.streakActual < this.streak) {
			this.streakActual = this.streak;
			this.streak = 0;
		}
	}
}
