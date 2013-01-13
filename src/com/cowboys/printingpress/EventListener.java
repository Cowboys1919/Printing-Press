package com.cowboys.printingpress;

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
	public Main M;
	
	public EventListener()
	{
		M = Main.getPl();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent E)
	{
		if (E.getClickedBlock() == null || E.getPlayer().getItemInHand() == null)
			return;
		
		if (E.getClickedBlock().getType() == M.conf.PressBlock && E.getAction() == Action.LEFT_CLICK_BLOCK && E.getClickedBlock().getRelative(0, 2, 0).getType() == Material.PISTON_BASE)
		{
			PistonBaseMaterial piston = new PistonBaseMaterial(Material.PISTON_BASE, E.getClickedBlock().getRelative(0, 2, 0).getData());
			if (E.getPlayer().getItemInHand().getType() == Material.BOOK)
				Utility.playerPrintBook(E.getPlayer(), E.getClickedBlock(), piston);
			else if (E.getPlayer().getItemInHand().getType() == Material.WRITTEN_BOOK || E.getPlayer().getItemInHand().getType() == Material.ENCHANTED_BOOK)
				Utility.playerClearBook(E.getPlayer(), E.getClickedBlock(), piston);
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent E)
	{
		Block B = E.getBlockPlaced();
		if (B == null)
			return;
		//Alert the player when they create a printing press.
		
		//If a base block is placed, check for the piston -
		else if (B.getType() == M.conf.PressBlock)
		{
			PistonBaseMaterial piston = new PistonBaseMaterial(Material.PISTON_BASE, B.getRelative(0, 2, 0).getData());
			
			if (B.getRelative(0, 2, 0).getType() == Material.PISTON_BASE)
				if (piston.getFacing() == BlockFace.DOWN)
					Language.msg(E.getPlayer(), M.lang.PrintingPressCreated);
		}
	}
}
