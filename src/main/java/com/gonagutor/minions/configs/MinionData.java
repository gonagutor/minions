package com.gonagutor.minions.configs;

import java.util.List;
import java.util.Map;

import com.gonagutor.minions.minions.BaseMinion;
import com.gonagutor.minions.minions.BlockMinion;
import com.google.common.collect.ImmutableMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.SkullMeta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatColor;

@NoArgsConstructor @AllArgsConstructor
public class MinionData implements ConfigurationSerializable{
	public static enum MinionType {BLOCK_MINION}
	@Getter private String itemName;
	@Getter private List<String> itemLore;
	@Getter private MinionType minionType;
	@Getter private Recipe recipe;
	@Getter private int maxLevel;
	@Getter private String minionName;
	@Getter private Material dropMaterial;
	@Getter private Material blockMaterial;
	@Getter private Color leatherArmorColor;
	@Getter private String skullOwner;

	public static MinionData deserialize(Map<String, Object> map) {
		try {
			List<String> lore = null;
			if (map.get("item_lore") instanceof List<?>)
				lore = (List<String>) map.get("item_lore");
			if (lore == null) return null;
			for (String each : lore) {
				lore.set(lore.indexOf(each), ChatColor.translateAlternateColorCodes('&', each));
			}
			return new MinionData(
				ChatColor.translateAlternateColorCodes('&', (String) map.get("item_name")),
				lore,
				MinionType.valueOf((String) map.get("minion_type")),
				null,//(Recipe) map.get("recipe")
				(int) map.get("max_level"),
				ChatColor.translateAlternateColorCodes('&', (String) map.get("minion_name")),
				Material.valueOf((String)map.get("drop_material")),
				Material.valueOf((String)map.get("block_material")),
				Color.deserialize((Map<String,Object>)map.get("minion_color")),
				(String)map.get("skull_owner")
			);
		} catch (ClassCastException e) {
			return null;
		}
	}

	public Map<String, Object> serialize() {
		return ImmutableMap.<String, Object>builder()
			.put("item_name", itemName)
			.put("item_lore", itemLore)
			.put("minion_type", minionType.toString())
			//.put("recipe", recipe)
			.put("max_level", maxLevel)
			.put("minion_name", minionName)
			.put("drop_material", dropMaterial.toString())
			.put("block_material", blockMaterial.toString())
			.put("minion_color", leatherArmorColor.serialize())
			.put("skull_owner", skullOwner)
			.build();
	}

	public ItemStack toSkull() {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		meta.setDisplayName(this.itemName);
		meta.setLore(this.itemLore);
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(this.skullOwner));
		item.setItemMeta(meta);
		return item;

	}

	public BaseMinion toMinion(Location loc, int level) {
		switch (minionType) {
			case BLOCK_MINION:
				return new BlockMinion(loc, this.blockMaterial, this.dropMaterial, level, minionName);
			default:
				return null;
		}
	}
}
