package com.gonagutor.minions.guis;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.api.BaseMinion;
import com.gonagutor.minions.managers.MinionManager;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
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
			inv.setItem(
				i,
				createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " ")
			);
		}
		int amount = minion.getItems();
		int slots = 0;
		// This is confusing so here I go.
		// This loops through the slots in which an an item can be placed
		// and places as much items as the minion has.
		// TODO: Add max slots by level. Right now it fills all 15 slots
		for (int z = 0; z < 9 * 3; z += 9) {
			for (int i = 21 + z; i < 26 + z; i++) {
				if (amount > 0) {
					if (amount / 64 > 0) {
						inv.setItem(
							i,
							new ItemStack(
								minion.getMinionData().getDropMaterial(),
								64
							)
						);
						amount -= 64;
					} else {
						inv.setItem(
							i,
							new ItemStack(
								minion.getMinionData().getDropMaterial(),
								amount
							)
						);
						amount = 0;
					}
				} else if (slots >= minion.getMaxSlots()) inv.setItem(
					i,
					createGuiItem(Material.WHITE_STAINED_GLASS_PANE, " ")
				); else inv.setItem(i, new ItemStack(Material.AIR));
				slots++;
			}
		}
		inv.setItem(
			3,
			createGuiItem(
				Material.REDSTONE_TORCH,
				"§e§lIdeal layout",
				"§7Click this item to",
				"§7show the ideal layout"
			)
		);
		inv.setItem(4, minion.getMinionData().toSkull(minion.getLevel()));
		inv.setItem(
			5,
			createGuiItem(
				Material.GOLD_INGOT,
				"§6§lView Recipe",
				"§7Click this item to",
				"§7view the recipe to make this minion"
			)
		);
		inv.setItem(
			10,
			createGuiItem(Material.LIME_STAINED_GLASS_PANE, "§2Skin slot")
		);
		inv.setItem(
			19,
			createGuiItem(Material.ORANGE_STAINED_GLASS_PANE, "§3Fuel slot")
		);
		inv.setItem(
			28,
			createGuiItem(Material.BLUE_STAINED_GLASS_PANE, "§3Output slot")
		);
		inv.setItem(
			37,
			createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "§eUpgrade slot")
		);
		inv.setItem(
			46,
			createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "§eUpgrade slot")
		);
		inv.setItem(
			48,
			createGuiItem(
				Material.CHEST,
				"§6§lPickup everything",
				"§7Click this item to",
				"§7pickup all the items and",
				"§7money from this minion"
			)
		);
		inv.setItem(
			50,
			createGuiItem(
				Material.DIAMOND,
				"§b§lUpgrade Minion",
				"§7Click this item to",
				"§7to upgrade this minion"
			)
		);
		inv.setItem(
			53,
			createGuiItem(
				Material.BEDROCK,
				"§c§lRemove Minion",
				"§7Click this item to",
				"§7pickup this minion"
			)
		);
	}

	/**
	 * Creates a new custom ItemStack
	 *
	 * @param material Material of the item
	 * @param name     Name of the item
	 * @param lore     Lore of the item
	 * @return Item stack with the properties requested
	 */
	protected ItemStack createGuiItem(
		final Material material,
		final String name,
		final String... lore
	) {
		final ItemStack item = new ItemStack(material, 1);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Opens this inventory to the given player
	 *
	 * @param ent Player to open the inventory to
	 */
	public void openInventory(final HumanEntity ent) {
		ent.openInventory(inv);
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) {
		if (e.getInventory() != inv) return;
		e.setCancelled(true);
		final ItemStack clickedItem = e.getCurrentItem();
		if (
			clickedItem == null || clickedItem.getType() == Material.AIR
		) return;
		if (e.getRawSlot() == 53) {
			minionManager.removeMinion(minion);
			Bukkit
				.getScheduler()
				.cancelTask(minion.getMinionTask().getTaskId());
			e.getWhoClicked().closeInventory();
			e
				.getWhoClicked()
				.getInventory()
				.addItem(minion.getMinionData().toSkull(minion.getLevel()));
			return;
		}

		if (
			e.getRawSlot() >= 21 &&
			e.getRawSlot() <= 44 &&
			e.getClickedInventory().getItem(e.getRawSlot()).getType() ==
			minion.getMinionData().getDropMaterial()
		) {
			ItemStack itemStack = e
				.getClickedInventory()
				.getItem(e.getRawSlot());
			Boolean hasFreeSpace = false;
			for (ItemStack space : e
				.getWhoClicked()
				.getInventory()
				.getStorageContents()) {
				if (space == null) {
					hasFreeSpace = true;
					break;
				}
			}

			if (hasFreeSpace) {
				e.getWhoClicked().getInventory().addItem(itemStack);
				minion.setItems(minion.getItems() - itemStack.getAmount());
				initializeItems();
				this.openInventory(e.getWhoClicked());
			} else {
				e
					.getWhoClicked()
					.sendMessage(
						Minions.getPrefix() +
						"You don't have enough free space on your inventory"
					);
				e.getWhoClicked().playEffect(EntityEffect.VILLAGER_ANGRY);
			}
			return;
		}
	}

	@EventHandler
	public void onInventoryClick(final InventoryDragEvent e) {
		if (e.getInventory() == inv) {
			e.setCancelled(true);
		}
	}
}
