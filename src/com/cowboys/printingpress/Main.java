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

public class Main extends JavaPlugin
{
	public Language lang;
	
	public ConfigurationValues conf;
	private FileConfiguration c;
	
	public static Main getPl()
	{
		return (Main) Bukkit.getPluginManager().getPlugin("PrintingPress");
	}
	
	public void onEnable()
	{
		conf = new ConfigurationValues();
		c = this.getConfig();
		
		final Map<String, Object> defaults = new LinkedHashMap<String, Object>();

		defaults.put("Language", "English");
		ArrayList<String> defaultPrintingCosts = new ArrayList<String>();
		ArrayList<String> defaultClearingCosts = new ArrayList<String>();
		
		//Default values

		defaults.put("Player.HungerLost", 3);
		defaults.put("Player.MustBeAuthor", true);
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
		
		//Update config
		this.saveConfig();
		
		//Create languages
		createLanguages();
		
		//Load config
		getValues();
	}
	
	private void createLanguages()
	{
		if (Language.makeLangIfNotExist("English")) getLogger().info("Created language file for 'English'");
		if (Language.makeLangIfNotExist("Portugese")) getLogger().info("Created language file for 'Portugese'");
		if (Language.makeLangIfNotExist("Spanish")) getLogger().info("Created language file for 'Spanish'");
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
		
		conf.Language = c.getString("Language");

		try
		{
			lang = Language.load(c.getString("Language"));
		}
		catch (FileNotFoundException e)
		{
			this.getLogger().severe("Language file not found: languages/'"+c.getString("Language")+".yml'");
		}
		catch (IOException e)
		{
			this.getLogger().severe("Could not read language file: languages/'"+c.getString("Language")+".yml'");
		}
		catch (InvalidConfigurationException e)
		{
			this.getLogger().severe("Invalid language file: languages/'"+c.getString("Language")+".yml'");
		}

		conf.PressBlock = Utility.getMat(c.getString("Press.Block"));
		conf.PressEffect = c.getBoolean("Press.Effect");

		conf.CanClearWrittenBooks = c.getBoolean("Press.ClearingOptions.CanClearWrittenBooks");
		conf.CanClearEnchantedBooks = c.getBoolean("Press.ClearingOptions.CanClearEnchantedBooks");

		Bukkit.getPluginManager().registerEvents(new EventListener(), this);
	}
		
	public void onDisable()
	{
		
	}
}
