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

	/**
	 * Retourne le nom long du mode de jeu
	 * @return
	 */
	public String toString() {
		return name;
	}
	
	/**
	 * Retourne le nom court du jeu
	 * @return
	 */
	public String getTag() {
		return tag;
	}
	
	/**
	 * Le mode de jeu est activé ?
	 * @return
	 */
	public boolean isEnabled(){
		return this.enable;
	}

	/**
	 * Retourne l'objet Gamemode
	 * @param tag
	 * @return
	 */
	public static Gamemode get(String tag){
	    for (Gamemode c : Gamemode.values()) {
	        if (c.tag.equalsIgnoreCase(tag)) {
	            return c;
	        }
	    }
	    return null;
	}
	
	/**
	 * Savoir si la clé existe
	 * @param tag
	 * @return
	 */
	public static boolean contains(String tag) {
	    for (Gamemode c : Gamemode.values()) {
	        if (c.tag.equalsIgnoreCase(tag)) {
	            return true;
	        }
	    }
	    return false;
	}
}
