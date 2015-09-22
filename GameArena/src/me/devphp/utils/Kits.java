package me.devphp.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import me.devphp.iPlugin;

public class Kits {
	
	
	private iPlugin plugin;
	private String arena;

	public Kits(iPlugin plugin, String arenaName){
		this.plugin = plugin;
		this.arena = arenaName;
	}
	
	public void getKit(Player player, String kitName){
		
	}
	
	public void saveKit(Player player, String kitName){

		this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".inventory", 
			InventorySerializer.InventoryToString(player.getInventory())
		);
		
		if (player.getInventory().getBoots() != null){
			int bootsId = player.getInventory().getBoots().getTypeId();
			short bootsDurability = player.getInventory().getBoots().getDurability();
			
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".boots.id", bootsId);
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".boots.durability", bootsDurability);

	        Map<Enchantment,Integer> isEnch = player.getInventory().getBoots().getEnchantments();
	        if (isEnch.size() > 0)
	        {
	        	int i = 0;
	            for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
	            {
	            	i++;
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".boots.enchantment.line-" + i + ".id", ench.getKey().getId());
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".boots.enchantment.line-" + i + ".val", ench.getValue());				            	
	            }
	            
	            try {
					this.plugin.getConfig().save(this.plugin.getConfigFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}
		
		
		if (player.getInventory().getLeggings() != null){
			int leggingsId = player.getInventory().getBoots().getTypeId();
			short leggingsDurability = player.getInventory().getBoots().getDurability();
			
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".leggings.id", leggingsId);
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".leggings.durability", leggingsDurability);

	        Map<Enchantment,Integer> isEnch = player.getInventory().getBoots().getEnchantments();
	        if (isEnch.size() > 0)
	        {
	        	int i = 0;
	            for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
	            {
	            	i++;
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".leggings.enchantment.line-" + i + ".id", ench.getKey().getId());
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".leggings.enchantment.line-" + i + ".val", ench.getValue());				            	
	            }
	            
	            try {
					this.plugin.getConfig().save(this.plugin.getConfigFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}	


		if (player.getInventory().getChestplate() != null){
			int chestplateId = player.getInventory().getBoots().getTypeId();
			short chestplateDurability = player.getInventory().getBoots().getDurability();
			
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".chestplate.id", chestplateId);
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".chestplate.durability", chestplateDurability);

	        Map<Enchantment,Integer> isEnch = player.getInventory().getBoots().getEnchantments();
	        if (isEnch.size() > 0)
	        {
	        	int i = 0;
	            for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
	            {
	            	i++;
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".chestplate.enchantment.line-" + i + ".id", ench.getKey().getId());
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".chestplate.enchantment.line-" + i + ".val", ench.getValue());				            	
	            }
	        }
		}	
		

		if (player.getInventory().getHelmet() != null){
			int helmetId = player.getInventory().getBoots().getTypeId();
			short helmetDurability = player.getInventory().getBoots().getDurability();
			
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".helmet.id", helmetId);
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".helmet.durability", helmetDurability);

	        Map<Enchantment,Integer> isEnch = player.getInventory().getBoots().getEnchantments();
	        if (isEnch.size() > 0)
	        {
	        	int i = 0;
	            for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
	            {
	            	i++;
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".helmet.enchantment.line-" + i + ".id", ench.getKey().getId());
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".helmet.enchantment.line-" + i + ".val", ench.getValue());				            	
	            }
	            
	            try {
					this.plugin.getConfig().save(this.plugin.getConfigFile());
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
		}
	}
}
