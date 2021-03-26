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
import org.bukkit.inventory.meta.LeatherArmorMeta;
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

	@SuppressWarnings("unchecked")
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
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("ERRRRROR");
			return new MinionData();
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

	public Boolean isDataValid(){
		if (this.blockMaterial != null && this.dropMaterial != null && this.itemName != null && this.leatherArmorColor != null)
			return true;
		return false;
	}

	@SuppressWarnings("deprecation")
	public ItemStack toSkull() {
		Boolean isUrl = skullOwner.contains("http://") || skullOwner.contains("https://");
		ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
		if (isUrl) item = CustomSkull.skullFromImage(item, skullOwner);
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		meta.setDisplayName(this.itemName);
		meta.setLore(this.itemLore);
		if (!isUrl) meta.setOwningPlayer(Bukkit.getOfflinePlayer(this.skullOwner));
		item.setItemMeta(meta);
		return item;

	}

	public BaseMinion toMinion(Location loc, int level) {
		switch (minionType) {
			case BLOCK_MINION:
				BlockMinion minion = new BlockMinion(loc, this.blockMaterial, this.dropMaterial, level, minionName);
				if (leatherArmorColor != null) {
					ItemStack itemHelmet = new ItemStack(Material.LEATHER_HELMET);
					LeatherArmorMeta helmetMeta = (LeatherArmorMeta) itemHelmet.getItemMeta();
					helmetMeta.setColor(this.leatherArmorColor);
					itemHelmet.setItemMeta(helmetMeta);
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

					minion.setHead(itemHelmet);
					minion.setChest(itemChest);
					minion.setLegs(itemLegs);
					minion.setBoots(itemBoots);
				}
				return minion;
			default:
				return null;
		}
	}
}
