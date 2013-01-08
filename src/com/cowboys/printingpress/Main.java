package com.cowboys.printingpress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
	public int HungerLost;
	public boolean PlayerMustBeAuthor;

	public ArrayList<ItemStack> costs;
	
	private FileConfiguration c;
	
	public static Main getPl()
	{
		return (Main) Bukkit.getPluginManager().getPlugin("PrintingPress");
	}
	public void onEnable()
	{
		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
		
		c = this.getConfig();
		
		final Map<String, Object> defaults = new HashMap<String, Object>();
		//c.options().copyDefaults(true);
     
		ArrayList<String> defaultCosts = new ArrayList<String>();
		defaultCosts.add("INK_SACK/1");
		defaults.put("Costs", defaultCosts);
		defaults.put("Player.HungerLost", 2);
		defaults.put("Player.MustBeAuthor", true);
		
		
		
 
		for (final Entry<String, Object> e : defaults.entrySet())
			if (!c.contains(e.getKey()))
				c.set(e.getKey(), e.getValue());
		
		
		this.saveConfig();

		getValues();
	}
	public void getValues()
	{
		costs = new ArrayList<ItemStack>();
		List<String> items = c.getStringList("Costs");
		if (items != null)
		{
			for (String s : items)
			{
				String[] peices = s.split("/");

				Bukkit.getLogger().info(peices[0] + " and "+peices[1]);
				
				costs.add(new ItemStack(Material.getMaterial(peices[0]), Integer.parseInt(peices[1])));
			}
		}
		else
			costs.add(new ItemStack(Material.INK_SACK, 1));
		
		HungerLost = c.getInt("Player.HungerLost");
		PlayerMustBeAuthor = c.getBoolean("Player.MustBeAuthor");
	}
	public void onDisable()
	{
		
	}

}
