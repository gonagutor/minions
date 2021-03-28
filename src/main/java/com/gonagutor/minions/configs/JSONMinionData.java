package com.gonagutor.minions.configs;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.managers.MinionManager;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

public class JSONMinionData {
	@Getter @Setter private static MinionManager minionManager;

	public static MinionData deserialize(String key) {
		for (MinionData minion : minionManager.getMinionList()) {
			if (minion.getKey().equals(key)) return minion;
		}
		Bukkit.getLogger().severe(Minions.getPrefix() + ChatColor.RED + "There was a place minion with a key that no longer exists. (Missing key: " + key + ")");
		Bukkit.getLogger().severe(Minions.getPrefix() + ChatColor.RED + "This is a severe error, the plugin will disable it self until this is fixied.");
		Bukkit.getLogger().severe(Minions.getPrefix() + ChatColor.RED + "Posible solutions include removing minions containing the key from the file data.json or adding a new key under minions with the missing key name");
		Bukkit.getLogger().severe(Minions.getPrefix() + ChatColor.RED + "Other solution is to rollback the changes and use the admin command to remove all instances of this minion");
		Bukkit.getPluginManager().disablePlugin(minionManager.getPlugin());
		return null;
	}

	public static String serialize(MinionData minionData) {
		return minionData.getKey();
	} 
}
