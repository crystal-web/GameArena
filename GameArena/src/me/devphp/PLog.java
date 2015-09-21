package me.devphp;

import java.util.logging.Logger;

public class PLog {
	private Logger log = Logger.getLogger("Minecraft");
	private String prefix;
	private boolean isDebug;
	
	public PLog(String prefix, boolean isDebug){
		this.prefix = prefix;
		this.isDebug = isDebug;
		if (this.isDebug){
			this.info("Debug is set to true");
		}
	}
	
	public void info(String message){
		if (this.isDebug){
			this.log.info(this.prefix.trim() + "[debug] " + message);
		}
	}
	
	public void severe(String message){
		this.log.severe(this.prefix + message);
	}
}
