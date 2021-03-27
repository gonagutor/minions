package com.gonagutor.minions.guis;

import java.util.Arrays;

import com.gonagutor.minions.managers.MinionManager;
import com.gonagutor.minions.minions.BaseMinion;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
	private BaseMinion minion;
	private MinionManager minionManager;

	public MinionInteractGui(BaseMinion bm, MinionManager mm) {
		this.minion = bm;
		this.minionManager = mm;
		inv = Bukkit.createInventory(null, 54, minion.getMenuTitle());
		initializeItems();
	}

	public void initializeItems() {
		for (int i = 0; i < 54; i++) {
			inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " "));
		}
		int amount = minion.getItems();
		for (int z = 0; z < 9 * 3; z += 9) {
			for (int i = 21 + z; i < 26 + z; i++) {
				if (amount > 0) {
					if (amount / 64 > 0) {
						inv.setItem(i, new ItemStack(minion.getMinionData().getDropMaterial(), 64));
						amount -= 64;
					} else {
						inv.setItem(i, new ItemStack(minion.getMinionData().getDropMaterial(), amount));
						amount = 0;
					}
				}
				else
					inv.setItem(i, createGuiItem(Material.WHITE_STAINED_GLASS_PANE, " "));
			}
		}
		inv.setItem(3, createGuiItem(Material.REDSTONE_TORCH, "§e§lIdeal layout", "§7Click this item to", "§7show the ideal layout"));
		inv.setItem(4, minion.getMinion().getEquipment().getHelmet());
		inv.setItem(5, createGuiItem(Material.GOLD_INGOT, "§6§lView Recipe", "§7Click this item to", "§7view the recipe to make this minion"));
		inv.setItem(10, createGuiItem(Material.LIME_STAINED_GLASS_PANE, "§2Skin slot"));
		inv.setItem(19, createGuiItem(Material.ORANGE_STAINED_GLASS_PANE, "§3Fuel slot"));
		inv.setItem(28, createGuiItem(Material.BLUE_STAINED_GLASS_PANE, "§3Output slot"));
		inv.setItem(37, createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "§eUpgrade slot"));
		inv.setItem(46, createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "§eUpgrade slot"));
		inv.setItem(48, createGuiItem(Material.CHEST, "§6§lPickup everything", "§7Click this item to", "§7pickup all the items and", "§7money from this minion"));
		inv.setItem(50, createGuiItem(Material.DIAMOND, "§b§lUpgrade Minion", "§7Click this item to", "§7to upgrade this minion"));
		inv.setItem(53, createGuiItem(Material.BEDROCK, "§c§lRemove Minion", "§7Click this item to", "§7pickup this minion"));
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
		if (e.getRawSlot() == 53) {
			minionManager.removeMinion(minion);
			Bukkit.getScheduler().cancelTask(minion.getMinionTask().getTaskId());
			e.getWhoClicked().closeInventory();
			e.getWhoClicked().getInventory().addItem(minion.getMinion().getEquipment().getHelmet());
		}
	}

	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory() == inv) {
			e.setCancelled(true);
		}
	}
}