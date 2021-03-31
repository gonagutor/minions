package com.gonagutor.minions.configs;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.api.BaseMinion;
import com.gonagutor.minions.utils.UtilLibrary;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

@NoArgsConstructor
@AllArgsConstructor
public class MinionData implements ConfigurationSerializable {
	@Getter @Setter private String key;
	@Getter @Setter private String itemName;
	@Getter @Setter private List<String> itemLore;
	@Getter @Setter private String minionType;
	@Getter @Setter private Recipe recipe;
	@Getter @Setter private int maxLevel;
	@Getter @Setter private String minionName;
	@Getter @Setter private Material dropMaterial;
	@Getter @Setter private Material blockMaterial;
	@Getter @Setter private Color leatherArmorColor;
	@Getter @Setter private String skullOwner;
	@Getter @Setter private ItemStack chest;
	@Getter @Setter private ItemStack legs;
	@Getter @Setter private ItemStack boots;
	@Getter @Setter private ItemStack tool;

	public MinionData(String itemName, List<String> itemLore, String minionType, Recipe recipe, int maxLevel, String minionName, Material dropMaterial, Material blockMaterial, Color leatherArmorColor, String skullOwner, ItemStack chest, ItemStack legs, ItemStack boots, ItemStack tool) {
		this.itemName = itemName;
		this.itemLore = itemLore;
		this.minionType = minionType;
		this.recipe = null;
		this.maxLevel = maxLevel;
		this.minionName = minionName;
		this.dropMaterial = dropMaterial;
		this.blockMaterial = blockMaterial;
		this.leatherArmorColor = leatherArmorColor;
		this.skullOwner = skullOwner;
		this.chest = chest;
		this.legs = legs;
		this.boots = boots;
		this.tool = tool;
		if (leatherArmorColor != null) {
			ItemStack itemChest = new ItemStack(Material.LEATHER_CHESTPLATE);
			LeatherArmorMeta chestMeta = (LeatherArmorMeta) itemChest.getItemMeta();
			chestMeta.setColor(this.leatherArmorColor);
			itemChest.setItemMeta(chestMeta);
			ItemStack itemLegs = new ItemStack(Material.LEATHER_LEGGINGS);
			LeatherArmorMeta legsMeta = (LeatherArmorMeta) itemLegs.getItemMeta();
			legsMeta.setColor(this.leatherArmorColor);
			itemLegs.setItemMeta(legsMeta);
			ItemStack itemBoots = new ItemStack(Material.LEATHER_BOOTS);
			LeatherArmorMeta bootsMeta = (LeatherArmorMeta) itemBoots.getItemMeta();
			bootsMeta.setColor(this.leatherArmorColor);
			itemBoots.setItemMeta(bootsMeta);

			this.setChest(itemChest);
			this.setLegs(itemLegs);
			this.setBoots(itemBoots);
		}
	}

	/**
	 * Deserializes the config
	 *
	 * @param map Map to deserialize
	 * @return New MinionData based on the map
	 */
	@SuppressWarnings("unchecked")
	public static MinionData deserialize(Map<String, Object> map) {
		try {
			List<String> lore = null;
			if (map.get("item_lore") instanceof List<?>) lore = (List<String>) map.get("item_lore");
			if (lore == null) return null;
			for (String each : lore) {
				lore.set(lore.indexOf(each), ChatColor.translateAlternateColorCodes('&', each));
			}
			return new MinionData(
				ChatColor.translateAlternateColorCodes('&', (String) map.get("item_name")),
				lore,
				(String) map.get("minion_type"),
				null, // (Recipe) map.get("recipe")
				((Number) map.get("max_level")).intValue(),
				ChatColor.translateAlternateColorCodes('&', (String) map.get("minion_name")),
				Material.valueOf((String) map.get("drop_material")),
				Material.valueOf((String) map.get("block_material")),
				Color.deserialize((Map<String, Object>) map.get("minion_color")),
				(String) map.get("skull_owner"),
				new ItemStack(Material.valueOf((String) map.get("chestplate"))),
				new ItemStack(Material.valueOf((String) map.get("leggins"))),
				new ItemStack(Material.valueOf((String) map.get("boots"))),
				new ItemStack(Material.valueOf((String) map.get("tool")))
			);
		} catch (Exception e) {
			e.printStackTrace();
			return new MinionData();
		}
	}

	/**
	 * This deserializes the minion data to be saved in config
	 *
	 * @return Map of this MinionData
	 */
	public Map<String, Object> serialize() {
		return ImmutableMap.<String, Object>builder()
			.put("item_name", itemName)
			.put("item_lore", itemLore)
			.put("minion_type", minionType)
			// .put("recipe", recipe)
			.put("max_level", maxLevel)
			.put("minion_name", minionName)
			.put("drop_material", dropMaterial.toString())
			.put("block_material", blockMaterial.toString())
			.put("minion_color", leatherArmorColor.serialize())
			.put("skull_owner", skullOwner)
			.put("chestplate", chest.getType().toString())
			.put("leggins", legs.getType().toString())
			.put("boots", boots.getType().toString())
			.put("tool", tool.getType().toString())
			.build();
	}

	/**
	 * This checks if the config data is valid. It is not yet properly implemented
	 *
	 * @return true or false
	 */
	public Boolean isDataValid() {
		if (
			this.blockMaterial != null &&
			this.dropMaterial != null &&
			this.itemName != null &&
			this.leatherArmorColor != null
		) return true;
		return false;
	}

	/**
	 * Get the assigned skull of this minion
	 *
	 * @return Skull ItemStack
	 */
	@SuppressWarnings("deprecation")
	public ItemStack toSkull(int level) {
		Boolean isUrl = skullOwner.contains("http://") || skullOwner.contains("https://");
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);

		if (isUrl) item = CustomSkull.skullFromImage(item, skullOwner);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setDisplayName(this.itemName + " level " + UtilLibrary.intToRoman(level));
		meta.setLore(this.itemLore);
		NamespacedKey minionType = new NamespacedKey(Minions.getPlugin(Minions.class), "minion_type");
		meta.getPersistentDataContainer().set(minionType, PersistentDataType.STRING, this.minionType);
		NamespacedKey minionLevel = new NamespacedKey(Minions.getPlugin(Minions.class), "minion_level");
		meta.getPersistentDataContainer().set(minionLevel, PersistentDataType.INTEGER, level);

		if (!isUrl) meta.setOwningPlayer(Bukkit.getOfflinePlayer(this.skullOwner));
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Creates a minion based on this MinionData
	 *
	 * @param loc    Position in which to create the minion
	 * @param level  Level of the minion to create
	 * @param player UUID of the owner
	 * @return Minion with specified parameters
	 */
	public BaseMinion toMinion(Location loc, int level, UUID player) {
		try {
			return (BaseMinion) Class.forName(minionType)
				.getConstructor(Location.class, MinionData.class, int.class, int.class, long.class, UUID.class)
				.newInstance(loc, this, level, 0, 0, player);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
