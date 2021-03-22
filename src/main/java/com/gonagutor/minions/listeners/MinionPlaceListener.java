package com.gonagutor.minions.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class MinionPlaceListener implements Listener {
	@EventHandler
	public void onSkullPlace(BlockPlaceEvent e) {
		if (e.getBlock().getType() != Material.PLAYER_HEAD) return;
		e.setCancelled(true);
		Location minionLocation = e.getBlock().getLocation().add(0.5, 0, 0.5);
		ArmorStand minion = (ArmorStand)minionLocation.getWorld().spawnEntity(minionLocation, EntityType.ARMOR_STAND);
		minion.setSmall(true);
		minion.setInvulnerable(true);
		minion.setGravity(false);
		minion.getEquipment().setHelmet(new ItemStack(Material.PLAYER_HEAD));
		minion.addEquipmentLock(EquipmentSlot.HAND, LockType.REMOVING_OR_CHANGING);
		minion.addEquipmentLock(EquipmentSlot.HEAD, LockType.REMOVING_OR_CHANGING);
		minion.addEquipmentLock(EquipmentSlot.CHEST, LockType.REMOVING_OR_CHANGING);
		minion.addEquipmentLock(EquipmentSlot.LEGS, LockType.REMOVING_OR_CHANGING);
		minion.addEquipmentLock(EquipmentSlot.FEET, LockType.REMOVING_OR_CHANGING);
	}
}
