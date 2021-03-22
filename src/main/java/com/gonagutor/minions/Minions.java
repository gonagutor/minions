package com.gonagutor.minions;

import com.gonagutor.minions.listeners.MinionPlaceListener;
import com.gonagutor.minions.managers.MinionManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public final class Minions extends JavaPlugin {
	@Getter private static String prefix = ChatColor.translateAlternateColorCodes('&', "&a[&6&lMinion&a] "); 
	private MinionManager minionManager;
	@Override
	public void onEnable() {
		minionManager = new MinionManager(this);
		Bukkit.getPluginManager().registerEvents(new MinionPlaceListener(minionManager), this);
		Bukkit.getConsoleSender().sendMessage(prefix + "The plugin has been enabled");
	}

	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(prefix + "The plugin has been disabled");
	}
}
