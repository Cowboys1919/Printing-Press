package com.cowboys.printingpress;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.PistonBaseMaterial;


public class Utility {

	@SuppressWarnings("deprecation")
	public static void playerPrintBook(Player player, Block clickedBlock, PistonBaseMaterial block) {
		Main M = Main.getPl();
		Inventory I = player.getInventory();
		
		if (block.getFacing() != BlockFace.DOWN)
		{
			player.sendMessage(ChatColor.AQUA+"The piston on the press is not facing the right way.");
			return;
		}
		if (player.getItemInHand() == null)
		{
			player.sendMessage(ChatColor.AQUA+"Right click the printing press with a normal book.");
			return;
		}
		if (player.getItemInHand().getType() != Material.BOOK)
		{
			player.sendMessage(ChatColor.AQUA+"Right click the printing press with a normal book.");
			return;
		}

		if (player.getItemInHand().getAmount() != 1)
		{
			player.sendMessage(ChatColor.AQUA+"The books cannot be stacked.");
			return;
		}
		if (player.getFoodLevel()<M.HungerLost)
		{
			player.sendMessage(ChatColor.AQUA+"You are too hungry.");
			return;
		}

		String itemsneeded = "";
		for (ItemStack item : M.costs)
		{
			itemsneeded += item.getAmount()+"x "+item.getType().toString().toLowerCase().replace('_', ' ');
		}
		if (!player.hasPermission("printingpress.freeuse"))
		{
			for (ItemStack item : M.costs)
			{	
				if (!I.contains(item.getType()))
				{
					player.sendMessage(ChatColor.AQUA+"You dont have enough resources.");
					player.sendMessage(ChatColor.AQUA+"Items needed: "+itemsneeded);
					return;
				}
				if (I.getItem(I.first(item.getType())).getAmount() < item.getAmount())
				{
					player.sendMessage(ChatColor.AQUA+"You dont have enough resources.");
					player.sendMessage(ChatColor.AQUA+"Items needed: "+itemsneeded);
					return;
				}
			}
		}
		if (I.getItem(0) == null)
		{
			player.sendMessage(ChatColor.AQUA+"Place a written book in the first slot in your inventory.");
			return;
		}
		if (I.getItem(0).getType() != Material.WRITTEN_BOOK)
		{
			player.sendMessage(ChatColor.AQUA+"Place a written book in the first slot in your inventory.");
			return;
		}
		
		BookMeta m = (BookMeta) I.getItem(0).getItemMeta();
		
		if (!m.getAuthor().equalsIgnoreCase(player.getName()) && M.getConfig().getBoolean("PlayerMustBeAuthor", true))
		{
			player.sendMessage(ChatColor.AQUA+"You must be the author of the book to copy it!");
			return;
		}
		
		block.setPowered(true);
		clickedBlock.getRelative(0, 2, 0).setData(block.getData(), true);
		clickedBlock.getRelative(0, 1, 0).setType(Material.PISTON_EXTENSION);
		
		ItemStack newBookItem = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta newBook = (BookMeta) newBookItem.getItemMeta();
		newBook.setAuthor(m.getAuthor());
		newBook.setTitle(m.getTitle());
		newBook.setPages(m.getPages());
		newBookItem.setItemMeta(newBook);
		player.setItemInHand(newBookItem);
		
		player.sendMessage(ChatColor.AQUA+"Book \""+m.getTitle()+"\" copied.");
		
		if (M.HungerLost != 0)
		{
			player.setFoodLevel(player.getFoodLevel()<=M.HungerLost?0:player.getFoodLevel()-M.HungerLost);
		}
		if (!player.hasPermission("printingpress.freeuse"))
		{
			for (ItemStack item : M.costs)
			{
				I.removeItem(item);
			}
		}
		
		player.updateInventory();
	}

}
