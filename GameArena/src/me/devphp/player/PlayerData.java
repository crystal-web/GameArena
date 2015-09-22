package me.devphp.player;

import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

public class PlayerData {
    private Player player;
    
    private double health;
    private int food, level;
    private float exp;
    private GameMode mode  = null;
    private Location loc = null;
    private Collection<PotionEffect> potions;

	private ItemStack[] inventory;

	private ItemStack[] armor;
    
    public PlayerData(Player player) {
        this.player		= player;
        this.mode   	= player.getGameMode();
        this.health		= player.getHealth();
        this.food		= player.getFoodLevel();
        this.level		= player.getLevel();
        this.exp		= player.getExp();
        this.loc		= player.getLocation();
        this.potions	= player.getActivePotionEffects();
        this.inventory	= player.getInventory().getContents();
        this.armor		= player.getInventory().getArmorContents();
    }
    
    public void restore(){
        player.setGameMode(this.mode);
        player.setHealth(this.health);
        player.setFoodLevel(this.food);
        player.setLevel(this.level);
        player.setExp(this.exp);
        player.teleport(this.loc);
		player.getInventory().setContents( this.inventory);
		player.getInventory().setArmorContents(this.armor);
		
		for (PotionEffect effect : this.potions){
			player.addPotionEffect(effect);
		}
        
    }
}
