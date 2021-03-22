package com.gonagutor.minions.listeners;

import com.gonagutor.minions.managers.MinionManager;
import com.gonagutor.minions.minions.BlockMinion;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class MinionPlaceListener implements Listener {
	private MinionManager minionManager;

	public MinionPlaceListener(MinionManager mm) {
		this.minionManager = mm;
	}

	@EventHandler
	public void onSkullPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() != Material.PLAYER_HEAD) return;
		e.setCancelled(true);
		Location minionLocation = e.getBlock().getLocation().add(0.5, 0, 0.5);
		BlockMinion minion = new BlockMinion(minionLocation, Material.DIAMOND_ORE, 5);
		minion.setHead(e.getItemInHand());
		minion.spawnMinion();
		Bukkit.getPluginManager().registerEvents(new MinionRightClickListener(minionManager, minion), minionManager.getPlugin());
	}
}
