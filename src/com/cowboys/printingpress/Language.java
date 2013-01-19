package com.cowboys.printingpress;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import com.google.common.io.ByteStreams;

public class Language
{
	//Set values
	public static final String[] Languages = { "English", "Spanish", "Portugese", "German", "French", "Russian" };
	

	//------------------Language Fields------------------
	public String PrintingPressCreated;
	public String BookCopied;
	public String DontHavePermission;
	public String DontHavePermissionToClear;
	public String DontHavePermissionToClearWrittenBooks;
	public String DontHavePermissionToClearEnchantedBooks;
	public String BookCleared;
	public String PistonWrongWay;
	public String LeftClickWithNormalBook;
	public String TooHungry;
	public String NotEnoughItems;
	public String ItemsNeeded;
	public String WrittenBookInFirstSlot;
	public String MustBeAuthor;
	public String DisableOnFactionOrWorldGuardLand;
	//----------------End Language Fields----------------
	
	
	public static Language get()
	{
		return Main.getPl().lang;
	}
	public static void msg(Player p, String s)
	{
		p.sendMessage(get(s));
	}
	public static String get(String s)
	{
		return ChatColor.AQUA+s;
	}
	
	public static boolean makeLangIfNotExist(String s) throws IOException, InvalidConfigurationException
	{
		//If the language file for the specified language doesnt exist, we will make it,
		//pulling the translation file that is compiled from within the jar,
		//returning true if a new file was made.
		if (!(new File(getLanguageFileName(s)).exists()))
				return saveLanguage(s);
		return false;
	}
	
	private static String getLanguageFileName(String s)
	{
		//Get location of language file.
		return Main.getPl().getDataFolder()+"/languages/"+s+".yml";
	}
	public static boolean saveLanguage(String language)
	{
		//Make sure language folder was made
		checkLanguageFolderMade();
		
		try
		{
			//Load the language files from the jar.
			JarFile jarFile = new JarFile(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
			JarEntry entry = jarFile.getJarEntry("Languages/"+language+".yml");
			
			//Get jar stream and output stream
			InputStream in = jarFile.getInputStream(entry);
			OutputStream out = new FileOutputStream(getLanguageFileName(language));
			
			//Copy it
			ByteStreams.copy(in, out);
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//Return failure
			return false;
		}
	}
	private static void checkLanguageFolderMade() {
		//Make the language folder if it isnt already.
		File folder = new File(Main.getPl().getDataFolder()+"/languages");
		if (!folder.exists())
			folder.mkdir();
	}
	
	private static String[] readFile(String file, int lines, String language) throws IOException 
	{
		String encoding = getEncodingForLanguage(language);
		BufferedReader in;
		if (encoding == null)
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		else
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
			
		String str;
 
		StringBuilder ret = new StringBuilder();
		
		while ((str = in.readLine()) != null)
		{
			ret.append(str).append("\n");
		}
		
		in.close();
		return ret.toString().split("\n");
	}
	
	private static String getEncodingForLanguage(String language) {
		if (language.equalsIgnoreCase("Russian"))
			return "UTF-8";
		return null;
	}
	
	public static Language load(String lang) throws FileNotFoundException, IOException, InvalidConfigurationException, IllegalArgumentException, IllegalAccessException
	{
		//Create the new language file that will be returned.
		Language l = new Language();
		
		//Get the lines of the 
		String[] lines = readFile(getLanguageFileName(lang), Language.class.getFields().length-1, lang);
		
		//Load for each field
		for (Field f : Language.class.getFields())
		{
			//Make sure we can access it
			f.setAccessible(true);
			//Skip the languages field that lists the current languages.
			if (!f.getName().equalsIgnoreCase("Languages"))
				f.set(l, getVal(lines, f.getName()));
		}
		
		//Return the loaded language.
		return l;
	}
	
	private static String getVal(String[] lines, String field)
	{
		for (String line : lines)
		{
			String[] part = line.split(":", 0);
			if (part[0].equalsIgnoreCase(field))
				return part[1];
		}
		return "No value set for "+field;
	}
}
