package com.gonagutor.minions.listeners;

import com.gonagutor.minions.configs.MinionData;
import com.gonagutor.minions.managers.MinionManager;
import com.gonagutor.minions.api.BaseMinion;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

// TODO: Improve skull recognition so player can't change the name of the skull or the lore by themselves
public class MinionPlaceListener implements Listener {
	private MinionManager minionManager;

	public MinionPlaceListener(MinionManager mm) {
		this.minionManager = mm;
	}

	@EventHandler
	public void onSkullPlace(BlockPlaceEvent e) {
		if (e.getItemInHand().getType() != Material.PLAYER_HEAD)
			return;
		MinionData mData = null;
		for (MinionData data : minionManager.getMinionList()) {
			if (data.toSkull().getItemMeta().getDisplayName()
					.equals(e.getItemInHand().getItemMeta().getDisplayName())) {
				mData = data;
				break;
			}
		}
		if (mData == null)
			return;
		e.setCancelled(true);

		Location minionLocation = e.getBlock().getLocation().add(0.5, 0, 0.5);
		BaseMinion minion = (BaseMinion) mData.toMinion(minionLocation, 10, e.getPlayer().getUniqueId());
		minionManager.newMinionPlaced(minion);
		minion.spawnMinion();
		Bukkit.getPluginManager().registerEvents(new MinionRightClickListener(minionManager, minion),
				minionManager.getPlugin());
	}
}
