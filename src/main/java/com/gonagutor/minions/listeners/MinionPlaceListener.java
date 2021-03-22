package com.gonagutor.minions.listeners;

import com.gonagutor.minions.minions.BlockMinion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class MinionPlaceListener implements Listener {
	@EventHandler
	public void onSkullPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() != Material.PLAYER_HEAD) return;
		e.setCancelled(true);
		Location minionLocation = e.getBlock().getLocation().add(0.5, 0, 0.5);
		BlockMinion minion = new BlockMinion(minionLocation);
		minion.setHead(e.getItemInHand());
		minion.spawnMinion();
	}
}
