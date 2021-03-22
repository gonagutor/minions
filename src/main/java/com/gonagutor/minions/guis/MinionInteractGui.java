package com.gonagutor.minions.guis;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MinionInteractGui implements Listener {
	private final Inventory inv;
	private ArmorStand entity;

	public MinionInteractGui(ArmorStand minion) {
		inv = Bukkit.createInventory(null, 9, "Example");
		initializeItems();
		this.entity = minion;
	}

	public void initializeItems() {
		inv.setItem(8, createGuiItem(Material.BEDROCK, "Delete Minion", "lore"));
	}

	protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}

	public void openInventory(final HumanEntity ent) {
		ent.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) {
		if (e.getInventory() != inv) return;
		e.setCancelled(true);
		final ItemStack clickedItem = e.getCurrentItem();
		if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
		if (e.getRawSlot() == 8) {
			entity.remove();
			e.getWhoClicked().closeInventory();
			e.getWhoClicked().getInventory().addItem(entity.getEquipment().getHelmet());
		}
	}

	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory() == inv) {
			e.setCancelled(true);
		}
	}
}