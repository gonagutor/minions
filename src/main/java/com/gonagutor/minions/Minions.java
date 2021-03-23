package com.gonagutor.minions;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.gonagutor.minions.commands.MinionsCommand;
import com.gonagutor.minions.configs.MinionConfig;
import com.gonagutor.minions.configs.MinionData;
import com.gonagutor.minions.configs.MinionData.MinionType;
import com.gonagutor.minions.listeners.MinionPlaceListener;
import com.gonagutor.minions.managers.MinionManager;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public final class Minions extends JavaPlugin {
	@Getter private static String prefix = ChatColor.translateAlternateColorCodes('&', "&a[&6&lMinion&a] "); 
	private MinionManager minionManager;
	@Getter MinionConfig minionConfig;
	@Getter Set<MinionData> minionList;

	@Override
	public void onEnable() {
		ConfigurationSerialization.registerClass(MinionData.class);
		this.minionManager = new MinionManager(this);
		this.minionConfig = new MinionConfig(this);
		minionConfig.reloadConfig();
		List<String> lore = Arrays.asList("&7Right click to place minion");
		this.minionConfig.addNewMinionToConfig("diamond", new MinionData(
			"&a&lDiamond minion",
			lore,
			MinionType.BLOCK_MINION,
			null,
			10,
			"&aDiamond minion",
			Material.DIAMOND,
			Material.DIAMOND_ORE,
			Color.AQUA,
			"DangerourMtp"
		));
		this.minionConfig.addNewMinionToConfig("wood", new MinionData(
			"&4&lOak minion",
			lore,
			MinionType.BLOCK_MINION,
			null,
			10,
			"&4Oak minion",
			Material.OAK_LOG,
			Material.OAK_LOG,
			Color.MAROON,
			"Technoblade"
		));
		this.minionList = minionConfig.getAllMinionsInConfig();
		this.getCommand("minions").setExecutor(new MinionsCommand(this));
		Bukkit.getPluginManager().registerEvents(new MinionPlaceListener(this.minionManager, this), this);
		Bukkit.getConsoleSender().sendMessage(prefix + "The plugin has been enabled");
	}

	@Override
	public void onDisable() {
		minionConfig.saveConfig();
		Bukkit.getConsoleSender().sendMessage(prefix + "The plugin has been disabled");
	}
}
