package com.cowboys.printingpress;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.P;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Main extends JavaPlugin
{
	public Language lang;
	
	public ConfigurationValues conf;
	public FileConfiguration c;
	public P factionsPlugin;
	public WorldGuardPlugin worldguardPlugin;
	
	public static Main getPl()
	{
		return (Main) Bukkit.getPluginManager().getPlugin("PrintingPress");
	}
	
	public void onEnable()
	{
		conf = new ConfigurationValues();
		c = this.getConfig();
		
		//Set hooks to other plugins
		factionsPlugin = (P) Bukkit.getServer().getPluginManager().getPlugin("Factions");
		worldguardPlugin = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		
		//Set defaults
		setDefaults();
		
		//Update config
		saveConfig();
		
		//Create languages that don't already exist before loading config
		try
		{
			createLanguages();
		}
		catch (IOException e){getLogger().severe("Could not load language, could not read the file.");}
		catch (InvalidConfigurationException e){getLogger().severe("Could not load language, invalid file.");}
		
		//Load config
		getValues();
	}
	
	
	public void setDefaults() {
		//Default objects
		final Map<String, Object> defaults = new LinkedHashMap<String, Object>();

		defaults.put("Language", "English");
		ArrayList<String> defaultPrintingCosts = new ArrayList<String>();
		ArrayList<String> defaultClearingCosts = new ArrayList<String>();
		
		
		//Default values
		defaults.put("Player.HungerLost", 3);

		defaults.put("Protection.DisableOnFactionLand", false);
		defaults.put("Protection.DisableOnWorldGuardLand", false);
		
		defaults.put("Press.Block", "IRON_BLOCK");
		defaults.put("Press.Effect", true);
		defaults.put("Press.ClearingOptions.CanClearWrittenBooks", true);
		defaults.put("Press.ClearingOptions.CanClearEnchantedBooks", true);
		
		defaultPrintingCosts.add("INK_SACK/1");
		defaults.put("PrintingCosts", defaultPrintingCosts);
		
		defaultClearingCosts.add("351:15/3");
		defaults.put("ClearingCosts", defaultClearingCosts);
		
		
		//Replace empty values with defaults
		for (final Entry<String, Object> e : defaults.entrySet())
			if (!c.contains(e.getKey()))
				c.set(e.getKey(), e.getValue());
	}
	public void createLanguages() throws IOException, InvalidConfigurationException
	{
		//Check each language and send a message if it was made and didnt exist.
		for (String S : Language.Languages)
			if (Language.makeLangIfNotExist(S)) getLogger().info("Created language file for '"+S+"'");
	}
	public void getValues()
	{
		//Cost for printing
		conf.printingCosts = new ArrayList<ItemStack>();
		List<String> items = c.getStringList("PrintingCosts");
		
		//Custom costs:
		if (items != null)
			for (String s : items)
				conf.printingCosts.add(Utility.getItem(s));
		//Default costs:
		else
			conf.printingCosts.add(new ItemStack(Material.INK_SACK, 1));
		
		//Cost for clearing
		conf.clearingCosts = new ArrayList<ItemStack>();
		items = c.getStringList("ClearingCosts");
		
		//Custom costs:
		if (items != null)
			for (String s : items)
				conf.clearingCosts.add(Utility.getItem(s));
		//Default costs:
		else
			conf.clearingCosts.add(new ItemStack(351, 3, (short)15));
		
		
		conf.HungerLost = c.getInt("Player.HungerLost");
		conf.MustBeAuthor= c.getBoolean("Player.MustBeAuthor");
		conf.PressEffect = c.getBoolean("Press.Effect");
		

		conf.DisableOnFactionLand = c.getBoolean("Protection.DisableOnFactionLand");
		conf.DisableOnWorldGuardLand = c.getBoolean("Protection.DisableOnWorldGuardLand");
		
		conf.Language = c.getString("Language");

		//Attempt to load the language and display a message if it failed.
		try
		{
			lang = Language.load(c.getString("Language"));
		}
		catch (FileNotFoundException e){this.getLogger().severe("Language file not found: languages/"+c.getString("Language")+".yml");e.printStackTrace(); }
		catch (IOException e){this.getLogger().severe("Could not read language file: languages/"+c.getString("Language")+".yml");e.printStackTrace(); }
		catch (InvalidConfigurationException e){this.getLogger().severe("Invalid language file: languages/"+c.getString("Language")+".yml");e.printStackTrace(); }
		catch (IllegalArgumentException e){this.getLogger().severe("Illegal values in language file: languages/"+c.getString("Language")+".yml");e.printStackTrace(); }
		catch (IllegalAccessException e){this.getLogger().severe("Can't access values in language file: languages/"+c.getString("Language")+".yml");e.printStackTrace(); }

		
		conf.PressBlock = Utility.getMat(c.getString("Press.Block"));
		conf.PressEffect = c.getBoolean("Press.Effect");

		conf.CanClearWrittenBooks = c.getBoolean("Press.ClearingOptions.CanClearWrittenBooks");
		conf.CanClearEnchantedBooks = c.getBoolean("Press.ClearingOptions.CanClearEnchantedBooks");

		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
	}
}
