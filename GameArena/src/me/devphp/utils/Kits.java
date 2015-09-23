package me.devphp.utils;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.devphp.iPlugin;

public class Kits {
	
	
	private iPlugin plugin;
	private String arena;

	public Kits(iPlugin plugin, String arenaName){
		this.plugin = plugin;
		this.arena = arenaName;
	}
	
	public void getKit(Player player, String kitName){
		
		if (!this.plugin.getConfig().contains("arena." + this.arena + ".kits." + kitName)){
			player.sendMessage(this.plugin.getPrefix() + "Unknow kit");
			player.sendMessage(this.plugin.getPrefix() + "Use " + ChatColor.GOLD + "/arena get kit list");
			return;
		}
		
		player.getInventory().clear();
		
		 if (this.plugin.getConfig().contains("arena." + this.arena + ".kits." + kitName + ".inventory")){
				player.getInventory().setContents(
					InventorySerializer.StringToInventory(
						this.plugin.getConfig().getString("arena." + this.arena + ".kits." + kitName + ".inventory")
					).getContents()
				);			
		 }

		if (this.plugin.getConfig().contains("arena." + this.arena + ".kits." + kitName + ".boots")){
			int bootsId = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".boots.id");
			short bootsDurability = (short) this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".boots.durability");
			
			ItemStack boots = new ItemStack( Material.getMaterial( bootsId ) );
			boots.setDurability( bootsDurability );
			if (this.plugin.getConfig().contains("arena." + this.arena + ".kits." + kitName + ".boots.enchantment")){
				for (String sec : this.plugin.getConfig().getConfigurationSection("arena." + this.arena + ".kits." + kitName + ".boots.enchantment").getKeys(false)){
	            	int id = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".boots.enchantment." + sec + ".id");
	            	int val = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".boots.enchantment." + sec + ".val");
	            	boots.addEnchantment(Enchantment.getById( id ), val);
				}
			}
			player.getInventory().setBoots(boots);
		}

		
		if (this.plugin.getConfig().contains("arena." + this.arena + ".kits." + kitName + ".leggings")){
			int leggingsId = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".leggings.id");
			short leggingsDurability = (short) this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".leggings.durability");
			
			ItemStack leggings = new ItemStack( Material.getMaterial( leggingsId ) );
			leggings.setDurability( leggingsDurability );
			if (this.plugin.getConfig().contains("arena." + this.arena + ".kits." + kitName + ".leggings.enchantment")){
				for (String sec : this.plugin.getConfig().getConfigurationSection("arena." + this.arena + ".kits." + kitName + ".leggings.enchantment").getKeys(false)){
	            	int id = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".leggings.enchantment." + sec + ".id");
	            	int val = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".leggings.enchantment." + sec + ".val");
	            	leggings.addEnchantment(Enchantment.getById( id ), val);
				}
			}
			
			player.getInventory().setLeggings(leggings);
		}

		
		if (this.plugin.getConfig().contains("arena." + this.arena + ".kits." + kitName + ".chestplate")){
			int chestplateId = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".chestplate.id");
			short chestplateDurability = (short) this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".chestplate.durability");
			
			ItemStack chestplate = new ItemStack( Material.getMaterial( chestplateId ) );
			chestplate.setDurability( chestplateDurability );
			if (this.plugin.getConfig().contains("arena." + this.arena + ".kits." + kitName + ".chestplate.enchantment")){
				for (String sec : this.plugin.getConfig().getConfigurationSection("arena." + this.arena + ".kits." + kitName + ".chestplate.enchantment").getKeys(false)){
	            	int id = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".chestplate.enchantment." + sec + ".id");
	            	int val = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".chestplate.enchantment." + sec + ".val");
	            	chestplate.addEnchantment(Enchantment.getById( id ), val);
				}
			}
			
			player.getInventory().setChestplate(chestplate);
		}

		
		if (this.plugin.getConfig().contains("arena." + this.arena + ".kits." + kitName + ".helmet")){
			int helmetId = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".helmet.id");
			short helmetDurability = (short) this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".helmet.durability");
			
			ItemStack helmet = new ItemStack( Material.getMaterial( helmetId ) );
			helmet.setDurability( helmetDurability );
			if (this.plugin.getConfig().contains("arena." + this.arena + ".kits." + kitName + ".chestplate.enchantment")){
				for (String sec : this.plugin.getConfig().getConfigurationSection("arena." + this.arena + ".kits." + kitName + ".helmet.enchantment").getKeys(false)){
	            	int id = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".helmet.enchantment." + sec + ".id");
	            	int val = this.plugin.getConfig().getInt("arena." + this.arena + ".kits." + kitName + ".helmet.enchantment." + sec + ".val");
	            	helmet.addEnchantment(Enchantment.getById( id ), val);
				}
			}
			
			player.getInventory().setHelmet(helmet);
		}
		
		
		
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
	        }
		}
		
		
		if (player.getInventory().getHelmet() != null){
			int helmetId = player.getInventory().getHelmet().getTypeId();
			short helmetDurability = player.getInventory().getHelmet().getDurability();
			
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".helmet.id", helmetId);
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".helmet.durability", helmetDurability);

	        Map<Enchantment,Integer> isEnch = player.getInventory().getHelmet().getEnchantments();
	        if (isEnch.size() > 0)
	        {
	        	int i = 0;
	            for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
	            {
	            	i++;
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".helmet.enchantment.line-" + i + ".id", ench.getKey().getId());
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".helmet.enchantment.line-" + i + ".val", ench.getValue());				            	
	            }
	        }
		}	


		if (player.getInventory().getChestplate() != null){
			int chestplateId = player.getInventory().getChestplate().getTypeId();
			short chestplateDurability = player.getInventory().getChestplate().getDurability();
			
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".chestplate.id", chestplateId);
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".chestplate.durability", chestplateDurability);

	        Map<Enchantment,Integer> isEnch = player.getInventory().getChestplate().getEnchantments();
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
		

		if (player.getInventory().getLeggings() != null){
			int leggingsId = player.getInventory().getLeggings().getTypeId();
			short leggingsDurability = player.getInventory().getLeggings().getDurability();
			
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".leggings.id", leggingsId);
			this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".leggings.durability", leggingsDurability);

	        Map<Enchantment,Integer> isEnch = player.getInventory().getHelmet().getEnchantments();
	        if (isEnch.size() > 0)
	        {
	        	int i = 0;
	            for (Entry<Enchantment,Integer> ench : isEnch.entrySet())
	            {
	            	i++;
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".leggings.enchantment.line-" + i + ".id", ench.getKey().getId());
	            	this.plugin.getConfig().set("arena." + this.arena + ".kits." + kitName + ".leggings.enchantment.line-" + i + ".val", ench.getValue());				            	
	            }
	        }
		}
	}

	public Set<String> getList() {
		if (this.plugin.getConfig().contains("arena." + this.arena + ".kits")){
			return this.plugin.getConfig().getConfigurationSection( "arena." + this.arena + ".kits" ).getKeys(false);
		}		
		return null;
	}
}
