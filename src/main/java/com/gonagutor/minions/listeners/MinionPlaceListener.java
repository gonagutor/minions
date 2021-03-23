package com.gonagutor.minions.listeners;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.configs.MinionData;
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
	private Minions minions;
	public MinionPlaceListener(MinionManager mm, Minions mini)  {
		this.minionManager = mm;
		this.minions = mini;
	}

	@EventHandler
	public void onSkullPlace(BlockPlaceEvent e) {
		if (e.getItemInHand().getType() != Material.PLAYER_HEAD) return;
		MinionData mData = null;
		for (MinionData data: minions.getMinionList()) {
			if (data.toSkull().getItemMeta().getDisplayName().equals(e.getItemInHand().getItemMeta().getDisplayName())) { 
				mData = data;
				break;
			}
		}
		if (mData == null) return;
		e.setCancelled(true);
		Location minionLocation = e.getBlock().getLocation().add(0.5, 0, 0.5);
		switch (mData.getMinionType()) {
			case BLOCK_MINION:
				BlockMinion minion = (BlockMinion) mData.toMinion(minionLocation, 10);
				minion.setHead(mData.toSkull());
				minion.spawnMinion();
				Bukkit.getPluginManager().registerEvents(new MinionRightClickListener(minionManager, minion), minionManager.getPlugin());
				break;
		
			default:
				break;
		}
	}
}
