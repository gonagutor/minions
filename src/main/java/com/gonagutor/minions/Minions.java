package com.gonagutor.minions;

import com.gonagutor.minions.commands.MinionsCommand;
import com.gonagutor.minions.configs.MinionData;
import com.gonagutor.minions.listeners.MinionPlaceListener;
import com.gonagutor.minions.managers.MinionManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public final class Minions extends JavaPlugin {
	@Getter private static String prefix = ChatColor.translateAlternateColorCodes('&', "&a[&6&lMinion&a] ");
	@Getter private MinionManager minionManager;

	@Override
	public void onEnable() {
		ConfigurationSerialization.registerClass(MinionData.class);
		this.minionManager = new MinionManager(this);

		minionManager.spawnAllMinions();
		this.getCommand("minions").setExecutor(new MinionsCommand(this.minionManager));
		Bukkit.getPluginManager().registerEvents(new MinionPlaceListener(this.minionManager), this);
		Bukkit.getConsoleSender().sendMessage(prefix + "The plugin has been enabled");
	}

	@Override
	public void onDisable() {
		minionManager.deSpawnAllMinions();
		Bukkit.getConsoleSender().sendMessage(prefix + "The plugin has been disabled");
	}
}
