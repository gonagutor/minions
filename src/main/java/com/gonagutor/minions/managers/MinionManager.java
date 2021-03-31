package com.gonagutor.minions.managers;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.api.BaseMinion;
import com.gonagutor.minions.configs.DataFile;
import com.gonagutor.minions.configs.JSONMinionData;
import com.gonagutor.minions.configs.MinionConfig;
import com.gonagutor.minions.configs.MinionData;
import com.gonagutor.minions.listeners.MinionRightClickListener;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

public class MinionManager {
	@Getter @Setter private Set<BaseMinion> minions = new HashSet<>();
	@Getter private Minions plugin;
	@Getter MinionConfig minionConfig;
	@Getter Set<MinionData> minionList;
	@Getter DataFile dataFile;
	@Getter Set<BaseMinion> loadedMinions = new HashSet<>();

	public MinionManager(Minions pl) {
		this.plugin = pl;
		this.minionConfig = new MinionConfig(this.plugin);
		minionConfig.reloadConfig();
		JSONMinionData.setMinionManager(this);
		this.minionList = minionConfig.getAllMinionsInConfig();
		this.dataFile = new DataFile(this.plugin);
		dataFile.reloadConfig();
	}

	/**
	 * Spawns all the minions found in config
	 */
	public void spawnAllMinions() {
		this.loadedMinions = dataFile.getPlayersMinions();
		for (BaseMinion minion : loadedMinions) {
			minion.spawnMinion();
			Bukkit.getPluginManager().registerEvents(new MinionRightClickListener(this, minion), this.getPlugin());
		}
	}

	/**
	 * Despawns all minions to be respawned on server load
	 */
	public void deSpawnAllMinions() {
		if (loadedMinions == null) return;
		dataFile.savePlayersMinions(loadedMinions);
		for (BaseMinion minion : loadedMinions) {
			minion.getMinion().remove();
		}
	}

	/**
	 * Add a minion to the loaded minions list
	 *
	 * @param minion Minion to save to config
	 */
	public void newMinionPlaced(BaseMinion minion) {
		this.loadedMinions.add(minion);
		dataFile.savePlayersMinions(loadedMinions);
	}

	/**
	 * Removes minion from loaded minions
	 *
	 * @param minion Minion to remove
	 */
	public void removeMinion(BaseMinion minion) {
		minion.getMinion().remove();
		loadedMinions.remove(minion);
	}
}
