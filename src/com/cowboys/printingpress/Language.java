package com.cowboys.printingpress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Language
{
	public static void msg(Player p, String s)
	{
		p.sendMessage(get(s));
	}
	public static String get(String s)
	{
		return ChatColor.AQUA+s;
	}
	String PrintingPressCreated;
	String BookCopied;
	String DontHavePermission;
	String DontHavePermissionToClear;
	String DontHavePermissionToClearWrittenBooks;
	String DontHavePermissionToClearEnchantedBooks;
	String BookCleared;
	String PistonWrongWay;
	String RightClickWithNormalBook;
	String TooHungry;
	String NotEnoughItems;
	String ItemsNeeded;
	String WrittenBookInFirstSlot;
	String MustBeAuthor;
	
	/*
	Template for new languages:
	
	private static boolean make[LanguageName]()
	{
		FileConfiguration c = new YamlConfiguration();
		
		c.set("PrintingPressCreated", "");
		c.set("BookCopied", " \"%\" ");
		c.set("DontHavePermission", "");
		c.set("DontHavePermissionToClear", "");
		c.set("DontHavePermissionToClearWrittenBooks", "");
		c.set("DontHavePermissionToClearEnchantedBooks", "");
		c.set("BookCleared", "");
		c.set("PistonWrongWay", "");
		c.set("RightClickWithNormalBook", "");
		c.set("TooHungry", "");
		c.set("NotEnoughItems", "");
		c.set("ItemsNeeded", "");
		c.set("WrittenBookInFirstSlot", "");
		c.set("MustBeAuthor", "");
		
		
		return saveLanguage(c, "[LanguageName]");
	}
	*/
	public static boolean makeLangIfNotExist(String s)
	{
		if (s.equals("English") && !(new File(getLanguageFileName(s))).exists())
			return makeEnglish();
		if (s.equals("Portugese") && !(new File(getLanguageFileName(s))).exists())
			return makePortugese();
		if (s.equals("Spanish") && !(new File(getLanguageFileName(s))).exists())
			return makeSpanish();
			
		
		return false;
	}
	
	//Languages:
	private static boolean makeEnglish()
	{
		FileConfiguration c = new YamlConfiguration();
		
		c.set("PrintingPressCreated", "Printing press created!");
		c.set("BookCopied", "Book \"%\" copied.");
		c.set("DontHavePermission", "You don't have permission to use the printing press!");
		c.set("DontHavePermissionToClear", "You don't have permission to clear books with the printing press!");
		c.set("DontHavePermissionToClearWrittenBooks", "You don't have permission to clear written books.");
		c.set("DontHavePermissionToClearEnchantedBooks", "You don't have permission to clear enchanted books.");
		c.set("BookCleared", "Book cleared.");
		c.set("PistonWrongWay", "The piston is not facing the right way.");
		c.set("RightClickWithNormalBook", "Right click the printing press with a normal book.");
		c.set("TooHungry", "You are too hungry.");
		c.set("NotEnoughItems", "You don't have enough resources.");
		c.set("ItemsNeeded", "Items needed: ");
		c.set("WrittenBookInFirstSlot", "Place a written book in the first slot in your inventory.");
		c.set("MustBeAuthor", "You must be the author of the book to copy it!");
		
		return saveLanguage(c, "English");
	}
	private static boolean makePortugese()
	{
		FileConfiguration c = new YamlConfiguration();
		
		c.set("PrintingPressCreated", "Copiadora criada!");
		c.set("BookCopied", "Livro \"%\" copiado.");
		c.set("DontHavePermission", "Não tens permissão para usar a Copiadora!");
		c.set("DontHavePermissionToClear", "Não tens permissão para remover o texto com a Copiadora!");
		c.set("DontHavePermissionToClearWrittenBooks", "Não tens permissão para remover o texto do livro!");
		c.set("DontHavePermissionToClearEnchantedBooks", "Não tens permissão para remover texto de livros encantados");
		c.set("BookCleared", "O texto do livro foi removido.");
		c.set("PistonWrongWay", "O pistão não está virado para o lado correcto.");
		c.set("RightClickWithNormalBook", "Click direito na Copiadora com um livro normal.");
		c.set("TooHungry", "Tens demasiada fome.");
		c.set("NotEnoughItems", "Não tens os items necessários.");
		c.set("ItemsNeeded", "Items necessários: ");
		c.set("WrittenBookInFirstSlot", "Coloca o livro escrito no primeiro espaço do teu inventário.");
		c.set("MustBeAuthor", "Tens que ser o autor do livro para copiá-lo.");
		
		return saveLanguage(c, "Portugese");
	}
	private static boolean makeSpanish()
	{
		FileConfiguration c = new YamlConfiguration();
		
		c.set("PrintingPressCreated", "¡Prensa de impresión creada!");
		c.set("BookCopied", "Libro \"%\" copiado.");
		c.set("DontHavePermission", "¡Usted no tiene permiso para utilizar la imprenta!");
		c.set("DontHavePermissionToClear", "¡No tienes permiso para borrar libros con la imprenta!");
		c.set("DontHavePermissionToClearWrittenBooks", "No tienes permiso para borrar los libros escritos.");
		c.set("DontHavePermissionToClearEnchantedBooks", "No tienes permiso para borrar libros encantados.");
		c.set("BookCleared", "Texto eliminado.");
		c.set("PistonWrongWay", "El pistón no está en la posición correcta.");
		c.set("RightClickWithNormalBook", "Clic derecho en la imprenta con un libro normal.");
		c.set("TooHungry", "Tienes demasiado hambre.");
		c.set("NotEnoughItems", "No tienes suficientes items.");
		c.set("ItemsNeeded", "Items necesarios: ");
		c.set("WrittenBookInFirstSlot", "Coloque un libro escrito en el primero espacio de tu inventario.");
		c.set("MustBeAuthor", "Necisitas ser el autor del libro para copiarlo.");
		
		return saveLanguage(c, "Spanish");
	}
	
	
	//Utilities:
	private static String getLanguageFileName(String s)
	{
		return Main.getPl().getDataFolder()+"/languages/"+s+".yml";
	}
	private static boolean saveLanguage(FileConfiguration c, String language)
	{

		checkLanguageFolderMade();
		try
		{
			c.save(Main.getPl().getDataFolder()+"/languages/"+language+".yml");
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	private static void checkLanguageFolderMade() {
		File folder = new File(Main.getPl().getDataFolder()+"/languages");
		if (!folder.exists())
			folder.mkdir();
	}
	
	//To load the languages in main:
	public static Language load(String lang) throws FileNotFoundException, IOException, InvalidConfigurationException {
		Language l = new Language();
		FileConfiguration c = new YamlConfiguration();
		c.load(new File(Main.getPl().getDataFolder()+"/languages/"+lang+".yml"));
		
		l.PrintingPressCreated = c.getString("PrintingPressCreated");
		l.BookCopied = c.getString("BookCopied");
		l.DontHavePermission = c.getString("DontHavePermission");
		l.DontHavePermissionToClear = c.getString("DontHavePermissionToClear");
		l.DontHavePermissionToClearWrittenBooks = c.getString("DontHavePermissionToClearWrittenBooks");
		l.DontHavePermissionToClearEnchantedBooks = c.getString("DontHavePermissionToClearEnchantedBooks");
		l.BookCleared = c.getString("BookCleared");
		l.PistonWrongWay = c.getString("PistonWrongWay");
		l.RightClickWithNormalBook = c.getString("RightClickWithNormalBook");
		l.TooHungry = c.getString("TooHungry");
		l.NotEnoughItems = c.getString("NotEnoughItems");
		l.ItemsNeeded = c.getString("ItemsNeeded");
		l.WrittenBookInFirstSlot = c.getString("WrittenBookInFirstSlot");
		l.MustBeAuthor = c.getString("MustBeAuthor");
		
		return l;
	}
}
