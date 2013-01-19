package com.cowboys.printingpress;

import java.util.ArrayList;

import net.minecraft.server.v1_4_R1.LocaleLanguage;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.craftbukkit.v1_4_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_4_R1.inventory.CraftItemStack;

public class Utility
{
	//Method checked to see if String S is a valid number
	public static boolean isNumber(String s)  
	{  
		try  
		{
			@SuppressWarnings("unused")
			int i = Integer.parseInt(s);
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;  
	}
	
	
	//Useful functions to get an itemstack from a value in the form of [id]:[data]/[amount]
	//For example "351:15/5" is 5 bone meal.
	public static ItemStack getItem(String text)
	{
		String[] peices = text.split("/");
		String s = peices[0];
		if (s.contains(":"))
		{
			String[] first = s.split(":");
			return new ItemStack(getMat(first[0]), peices.length==2?Integer.valueOf(peices[1]):1, Short.valueOf(first[1]));
		}
		else
			if (isNumber(s))
				return new ItemStack(Material.getMaterial(Integer.valueOf(s)), peices.length==2?Integer.valueOf(peices[1]):1);
			else
				return new ItemStack(Material.getMaterial(s), peices.length==2?Integer.valueOf(peices[1]):1);
	}
	public static Material getMat(String text)
	{
		return isNumber(text)?Material.getMaterial(Integer.valueOf(text)):Material.getMaterial(text);
	}
	
	
	
	//Checks to see if the printing press is valid and if the player has the required hunger and items.
	public static boolean runChecks(Main M, Player player, Block clickedBlock, PistonBaseMaterial block, short checkID)
	{
		if (block.getFacing() != BlockFace.DOWN)
		{
			Language.msg(player, M.lang.PistonWrongWay);
			return false;
		}

		if (checkID == (short)0)
		{
			if (player.getItemInHand() == null)
			{
				Language.msg(player, M.lang.LeftClickWithNormalBook);
				return false;
			}
			if (player.getItemInHand().getType() != Material.BOOK)
			{
				Language.msg(player, M.lang.LeftClickWithNormalBook);
				return false;
			}
		}
		
		if (player.getFoodLevel()<M.conf.HungerLost)
		{
			Language.msg(player, M.lang.TooHungry);
			return false;
		}

		ArrayList<ItemStack> costs = null;
		if (checkID == (short)0)
			costs = M.conf.printingCosts;
		else if (checkID == (short)1)
			costs = M.conf.clearingCosts;
		else
			M.getLogger().severe("Invalid CheckID, tell the plugin developer.");

		//Check needed items
		String itemsneeded = "";
		LocaleLanguage l = ((CraftPlayer)player).getHandle().getLocale();
		
		for (ItemStack item : costs)
		{
			itemsneeded += item.getAmount()+"x "+l.b(CraftItemStack.asNMSCopy(item).a()+".name").replace('_', ' ').toLowerCase()+", ";
		}
		
		itemsneeded = itemsneeded.substring(0, itemsneeded.length()-2);
		

		PlayerInventory I = player.getInventory();
		
		if (!player.hasPermission("printingpress.freeuse"))
		{
			for (ItemStack item : costs)
			{	
				if (!I.containsAtLeast(item, item.getAmount()))
				{
					Language.msg(player, M.lang.NotEnoughItems);
					player.sendMessage(Language.get(M.lang.ItemsNeeded)+itemsneeded);
					return false;
				}
				if (I.getItem(I.first(item.getType())).getAmount() < item.getAmount() && I.getItem(I.first(item.getType())).getData().getData() != item.getData().getData())
				{
					Language.msg(player, M.lang.NotEnoughItems);
					player.sendMessage(Language.get(M.lang.ItemsNeeded)+itemsneeded);
					return false;
				}
			}
		}

		return true;
	}
	
	//Extends the printing press and players a smokey effect.
	private static void extendPress(Main M, Block clickedBlock, PistonBaseMaterial block) {
		block.setPowered(true);
		clickedBlock.getRelative(0, 2, 0).setData(block.getData(), true);
		clickedBlock.getRelative(0, 1, 0).setType(Material.PISTON_EXTENSION);
		
		if (M.conf.PressEffect)
			clickedBlock.getWorld().playEffect(clickedBlock.getLocation().add(0, 1, 0), Effect.MOBSPAWNER_FLAMES, 1);
	}

	
	//Prints book.
	@SuppressWarnings("deprecation")
	public static void playerPrintBook(Player player, Block clickedBlock, PistonBaseMaterial block)
	{
		Main M = Main.getPl();
		Inventory I = player.getInventory();
		
		//Check if they have permission to use the press:
		if (!player.hasPermission("printingpress.normaluse"))
		{
			Language.msg(player, M.lang.DontHavePermission);
			return;
		}
		

		//Run checks that apply to the printing press in general:
		if (!runChecks(M, player, clickedBlock, block, (short)0)) return;
		
		
		//Check for written book in first slot:
		if (I.getItem(0) == null)
		{
			Language.msg(player, M.lang.WrittenBookInFirstSlot);
			return;
		}
		if (I.getItem(0).getType() != Material.WRITTEN_BOOK)
		{
			Language.msg(player, M.lang.WrittenBookInFirstSlot);
			return;
		}
		
		//Check author is the same:
		BookMeta m = (BookMeta) I.getItem(0).getItemMeta();
		
		if (!m.getAuthor().equalsIgnoreCase(player.getName()) && M.conf.MustBeAuthor && !player.hasPermission("printingpress.bypassauthor"))
		{
			Language.msg(player, M.lang.MustBeAuthor);
			return;
		}
		
		//Passed all checks, print the book:
		
		//Extend the press:
		extendPress(M, clickedBlock, block);
		
		//Copy book onto a new book:
		ItemStack newBookItem = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta newBook = (BookMeta) newBookItem.getItemMeta();
		newBook.setAuthor(m.getAuthor());
		newBook.setTitle(m.getTitle());
		newBook.setPages(m.getPages());
		newBookItem.setItemMeta(newBook);
		
		//If it's a stack of 1, just replace the one:
		//If not, place it in the first available inventory space and if
		//no space is available, drop it on the ground.
		if (player.getItemInHand().getAmount() == 1)
			player.setItemInHand(newBookItem);
		else
		{
			player.getInventory().removeItem(new ItemStack(Material.BOOK, 1));
			if (player.getInventory().firstEmpty() == -1)
				clickedBlock.getWorld().dropItemNaturally(clickedBlock.getRelative(BlockFace.UP).getLocation(), newBookItem);
			else
				player.getInventory().setItem(player.getInventory().firstEmpty(), newBookItem); 
		}

		//Send the player a message about the book they copied
		String[] messagePeice = Language.get(M.lang.BookCopied).split("%");
		player.sendMessage(messagePeice[0]+m.getTitle()+messagePeice[1]);
		
		//Remove hunger points
		if (M.conf.HungerLost != 0)
			player.setFoodLevel(player.getFoodLevel()<=M.conf.HungerLost?0:player.getFoodLevel()-M.conf.HungerLost);
		
		//Remove items from their inventories
		if (!player.hasPermission("printingpress.freeuse"))
			for (ItemStack item : M.conf.printingCosts)
				I.removeItem(item);
		

		player.updateInventory();
	}
	
	//Clears book.
	@SuppressWarnings("deprecation")
	public static void playerClearBook(Player player, Block clickedBlock, PistonBaseMaterial block)
	{
		Main M = Main.getPl();
		PlayerInventory I = player.getInventory();
		
		if (!player.hasPermission("printingpress.clearinguse"))
		{
			Language.msg(player, M.lang.DontHavePermissionToClear);
			return;
		}
		
		//Run checks that apply to the printing press in general:
		if (!runChecks(M, player, clickedBlock, block, (short)1)) return;
		
		if (I.getItemInHand().getType() == Material.WRITTEN_BOOK && !M.conf.CanClearWrittenBooks)
		{
			Language.msg(player, M.lang.DontHavePermissionToClearWrittenBooks);
			return;
		}
		
		if (I.getItemInHand().getType() == Material.ENCHANTED_BOOK && !M.conf.CanClearEnchantedBooks)
		{
			Language.msg(player, M.lang.DontHavePermissionToClearEnchantedBooks);
			return;
		}
		
		//Passed all checks, clear the book:
		
		//Extend the press:
		extendPress(M, clickedBlock, block);
		
		player.setItemInHand(new ItemStack(Material.BOOK));
		
		//Remove hunger points
		if (M.conf.HungerLost != 0)
			player.setFoodLevel(player.getFoodLevel()<=M.conf.HungerLost?0:player.getFoodLevel()-M.conf.HungerLost);
		
		Language.msg(player, M.lang.BookCleared);
		
		//Remove items from their inventories
		if (!player.hasPermission("printingpress.freeuse"))
			for (ItemStack item : M.conf.clearingCosts)
				I.removeItem(item);
				
		
		player.updateInventory();
	}


	public static boolean checkProtection(Main M, PlayerInteractEvent E)
	{
		if (M.conf.DisableOnFactionLand)
			if (M.factionsPlugin != null)
				if (!M.factionsPlugin.isPlayerAllowedToBuildHere(E.getPlayer(), E.getClickedBlock().getLocation()))
				{
					Language.msg(E.getPlayer(), M.lang.DisableOnFactionOrWorldGuardLand);
					return false;
				}
		if (M.conf.DisableOnWorldGuardLand)
			if (M.worldguardPlugin != null)
				if (!M.worldguardPlugin.canBuild(E.getPlayer(), E.getClickedBlock()))
				{
					Language.msg(E.getPlayer(), M.lang.DisableOnFactionOrWorldGuardLand);
					return false;
				}
		return true;
	}

}
