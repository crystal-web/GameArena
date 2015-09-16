package me.devphp.ga.games;


public enum Gamemode {
	DEATHMATCH("Death Match", "dm", false), 
	TEAMDEATHMATCH("Team Death Match", "tdm", true),
	KILLTHEKING("Kill The King", "ktk", false),
	CAPTURETHEFLAG("Capture The Flag", "ctf", false);

	private String name = "";
	private String tag = "";
	private boolean enable = false;

	// Constructeur
	Gamemode(String name, String tag, boolean enabled) {
		this.name = name;
		this.tag = tag;
		this.enable  = enabled;
	}

	public String toString() {
		return name;
	}
	
	public String getTag() {
		return tag;
	}
	
	public boolean isEnabled(){
		return this.enable;
	}

	public static Gamemode get(String key){
	    for (Gamemode c : Gamemode.values()) {

	        if (c.tag.equalsIgnoreCase(key)) {
	            return c;
	        }
	        
	    }
	    return null;
	}
	
	public static boolean contains(String test) {
	    for (Gamemode c : Gamemode.values()) {
	        if (c.tag.equalsIgnoreCase(test)) {
	            return true;
	        }
	    }

	    return false;
	}
}
