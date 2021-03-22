package com.gonagutor.minions.listeners;

import com.gonagutor.minions.guis.MinionInteractGui;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.Plugin;

public class MinionRightClickListener implements Listener {
	private Plugin plugin;
	public MinionRightClickListener(Plugin pl) {
		this.plugin = pl;
	}

	@EventHandler
	public void onMinionRightClick(PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked().getType() != EntityType.ARMOR_STAND) return;
		MinionInteractGui migui = new MinionInteractGui((ArmorStand) e.getRightClicked());
		Bukkit.getPluginManager().registerEvents(migui, plugin);
		migui.openInventory(e.getPlayer());
	}
}
