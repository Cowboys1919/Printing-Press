package com.cowboys.printingpress;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.PistonBaseMaterial;

public class EventListener implements Listener
{
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent E)
	{
		if (E.getClickedBlock() == null)
			return;
		if (E.getClickedBlock().getType() == Material.IRON_BLOCK && E.getAction() == Action.RIGHT_CLICK_BLOCK && E.getClickedBlock().getRelative(0, 2, 0).getType() == Material.PISTON_BASE)
		{
			if (!E.getPlayer().hasPermission("printingpress.normaluse"))
			{
				E.getPlayer().sendMessage(ChatColor.AQUA+"You dont have permission to use the printing press.");
				return;
			}
			PistonBaseMaterial piston = new PistonBaseMaterial(Material.PISTON_BASE, E.getClickedBlock().getRelative(0, 2, 0).getData());
			Utility.playerPrintBook(E.getPlayer(), E.getClickedBlock(), piston);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent E)
	{
		Block B = E.getBlockPlaced();
		if (B == null)
			return;
		if (B.getType() == Material.PISTON_BASE)
		{
			PistonBaseMaterial piston = new PistonBaseMaterial(Material.PISTON_BASE, B.getData());
			if (piston.getFacing() == BlockFace.DOWN)
			{
				if (B.getRelative(0, -2, 0).getType() == Material.IRON_BLOCK)
				{
					E.getPlayer().sendMessage(ChatColor.AQUA+"Printing Press created!");
				}
			}
		}
		else if (B.getType() == Material.IRON_BLOCK)
		{
			if (B.getRelative(0, 2, 0).getType() == Material.PISTON_BASE)
			{
				PistonBaseMaterial piston = new PistonBaseMaterial(Material.PISTON_BASE, B.getRelative(0, 2, 0).getData());
				if (piston.getFacing() == BlockFace.DOWN)
				{
					E.getPlayer().sendMessage(ChatColor.AQUA+"Printing Press created!");
				}
			}
		}
	}
}
