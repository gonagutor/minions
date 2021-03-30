package com.gonagutor.minions.configs;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.managers.MinionManager;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

// This must be static because I couldn't find a way to implement another way.
// Right now this is filled once the available minion data has been read.
// This way I don't need to pass around an instance of the config to every minion which seemed janky

public class JSONMinionData {

	@Getter
	@Setter
	private static MinionManager minionManager;

	/**
	 * This functions deserializes a minion based on the key
	 *
	 * @param key The key of the minion inside the config. <b>Format:</b>
	 *            minion.[something]
	 * @return MinionData of the requested minion
	 */
	public static MinionData deserialize(String key) {
		for (MinionData minion : minionManager.getMinionList()) {
			if (minion.getKey().equals(key)) return minion;
		}
		Bukkit
			.getLogger()
			.severe(
				Minions.getPrefix() +
				ChatColor.RED +
				"There was a place minion with a key that no longer exists. (Missing key: " +
				key +
				")"
			);
		Bukkit
			.getLogger()
			.severe(
				Minions.getPrefix() +
				ChatColor.RED +
				"This is a severe error, the plugin will disable itself until this is fixed."
			);
		Bukkit
			.getLogger()
			.severe(
				Minions.getPrefix() +
				ChatColor.RED +
				"Possible solutions include removing minions containing the key from the file data.json or adding a new key under minions with the missing key name"
			);
		Bukkit
			.getLogger()
			.severe(
				Minions.getPrefix() +
				ChatColor.RED +
				"Other solution is to rollback the changes and use the admin command to remove all instances of this minion"
			);
		Bukkit.getPluginManager().disablePlugin(minionManager.getPlugin());
		return null;
	}

	/**
	 * This gets the key from the passed minion
	 *
	 * @param minionData MinionData to deserialize (Get the key from)
	 * @return Key of the minion
	 */
	public static String serialize(MinionData minionData) {
		return minionData.getKey();
	}
}
