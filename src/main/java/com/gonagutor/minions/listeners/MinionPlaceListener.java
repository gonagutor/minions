package com.gonagutor.minions.listeners;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.api.BaseMinion;
import com.gonagutor.minions.configs.MinionData;
import com.gonagutor.minions.managers.MinionManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataType;

public class MinionPlaceListener implements Listener {

	private MinionManager minionManager;

	public MinionPlaceListener(MinionManager mm) {
		this.minionManager = mm;
	}

	@EventHandler
	public void onSkullPlace(BlockPlaceEvent e) {
		if (e.getItemInHand().getType() != Material.PLAYER_HEAD) return;
		NamespacedKey minionType = new NamespacedKey(
			Minions.getPlugin(Minions.class),
			"minion_type"
		);
		if (
			!e
				.getItemInHand()
				.getItemMeta()
				.getPersistentDataContainer()
				.has(minionType, PersistentDataType.STRING)
		) return;
		NamespacedKey minionLevel = new NamespacedKey(
			Minions.getPlugin(Minions.class),
			"minion_level"
		);
		if (
			!e
				.getItemInHand()
				.getItemMeta()
				.getPersistentDataContainer()
				.has(minionLevel, PersistentDataType.INTEGER)
		) return;
		MinionData mData = null;
		int level = e
			.getItemInHand()
			.getItemMeta()
			.getPersistentDataContainer()
			.get(minionLevel, PersistentDataType.INTEGER);
		for (MinionData data : minionManager.getMinionList()) {
			if (
				data
					.toSkull(level)
					.getItemMeta()
					.getDisplayName()
					.equals(e.getItemInHand().getItemMeta().getDisplayName())
			) {
				mData = data;
				break;
			}
		}
		if (mData == null) return;
		e.setCancelled(true);
		if (
			e.getBlock().getLocation().subtract(0, 1, 0).getBlock().getType() ==
			Material.AIR
		) {
			e
				.getPlayer()
				.sendMessage(
					Minions.getPrefix() +
					ChatColor.RED +
					"You can't place a minion on the air"
				);
			return;
		}

		Location minionLocation = e.getBlock().getLocation().add(0.5, 0, 0.5);
		BaseMinion minion = (BaseMinion) mData.toMinion(
			minionLocation,
			level,
			e.getPlayer().getUniqueId()
		);
		minionManager.newMinionPlaced(minion);
		minion.spawnMinion();
		Bukkit
			.getPluginManager()
			.registerEvents(
				new MinionRightClickListener(minionManager, minion),
				minionManager.getPlugin()
			);
	}
}
