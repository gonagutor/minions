package com.gonagutor.minions.listeners;

import com.gonagutor.minions.guis.MinionInteractGui;
import com.gonagutor.minions.managers.MinionManager;
import com.gonagutor.minions.minions.BaseMinion;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class MinionRightClickListener implements Listener {
	private MinionManager minionManager;
	private BaseMinion minion;

	public MinionRightClickListener(MinionManager mm, BaseMinion mnn) {
		this.minionManager = mm;
		this.minion = mnn;
	}

	@EventHandler
	public void onMinionRightClick(PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked().getType() != EntityType.ARMOR_STAND)
			return;
		if (minion.getMinion() != e.getRightClicked())
			return;
		if (e.getPlayer().getUniqueId() != minion.getPlayerUuid())
			return;

		MinionInteractGui minionInteractGui = new MinionInteractGui(minion, minionManager);
		Bukkit.getPluginManager().registerEvents(minionInteractGui, minionManager.getPlugin());
		minionInteractGui.openInventory(e.getPlayer());
	}
}
