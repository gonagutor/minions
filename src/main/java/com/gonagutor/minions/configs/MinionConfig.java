package com.gonagutor.minions.configs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import com.gonagutor.minions.Minions;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

public class MinionConfig {
	private Minions plugin;
	private FileConfiguration dataConfig = null;
	private File configFile = null;
	@Getter private Set<MinionData> minionData;

	public MinionConfig(Minions pl) {
		this.plugin = pl;
		saveDefaultConfig();
	}

	public void reloadConfig() {
		if (this.configFile == null)
			this.configFile = new File(this.plugin.getDataFolder(), "minions.yml");
		this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
		InputStream defaultStream = this.plugin.getResource("minions.yml");
		if (defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.dataConfig.setDefaults(defaultConfig);
		}
	}

	public FileConfiguration getConfig() {
		if (this.dataConfig == null)
			reloadConfig();
		return this.dataConfig;
	}

	public void saveConfig() {
		if (this.dataConfig == null || this.configFile == null)
			return;
		try {
			this.getConfig().save(this.configFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, Minions.getPrefix() + "File could not be saved in: " + this.configFile, e);
		}
	}

	public void saveDefaultConfig() {
		if (this.configFile == null)
			this.configFile = new File(this.plugin.getDataFolder(), "minions.yml");
		if (!this.configFile.exists()) {
			this.plugin.saveResource("minions.yml", false);
		}
	}

	public Set<MinionData> getAllMinionsInConfig() {
		Set<MinionData> md = new HashSet<>();
		for (String key : this.getConfig().getRoot().getKeys(true)) {
			if (key.equals("minions")) continue;
			MinionData mData = (MinionData) this.getConfig().get(key, MinionData.class);
			if (!mData.isDataValid()) {
				wrongConfig(key);
				return getAllMinionsInConfig();
			}
			mData.setKey(key);
			md.add(mData);
		}
		return md;
	}

	public void addNewMinionToConfig(String branch, MinionData md) {
		this.getConfig().set("minions." + branch, md);; // Deserialize using .deserialize() to not show the == thingy
		md.setKey(branch);
		this.saveConfig();
		this.reloadConfig();
	}

	public void wrongConfig(String errorField) {
		this.plugin.getLogger().log(Level.SEVERE, Minions.getPrefix() + ChatColor.RED + "A config error was found on minion \"" + errorField + "\"");
		this.plugin.getLogger().log(Level.SEVERE, ChatColor.RED + "The config has been restored to the default config and a new file called minions_old.yml was created with your old config.");
		this.plugin.getLogger().log(Level.SEVERE, ChatColor.RED + "Please fix this and try again");
		try {
			Files.move(Path.of(this.plugin.getDataFolder().getAbsolutePath() + "/minions.yml"), Path.of(this.plugin.getDataFolder().getAbsolutePath() + "/minions_old.yml"), StandardCopyOption.REPLACE_EXISTING);
			this.saveDefaultConfig();
			this.reloadConfig();
		} catch (Exception e) {
			this.plugin.getLogger().log(Level.SEVERE, "Error renaming config file. Mantaining old file. \nError stack trace: " + e.toString());
		}
	}
}
