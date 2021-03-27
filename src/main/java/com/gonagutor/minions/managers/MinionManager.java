package com.gonagutor.minions.managers;

import java.util.HashSet;
import java.util.Set;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.configs.DataFile;
import com.gonagutor.minions.configs.MinionConfig;
import com.gonagutor.minions.configs.MinionData;
import com.gonagutor.minions.minions.BaseMinion;

import lombok.Getter;
import lombok.Setter;

public class MinionManager {
	@Getter @Setter private Set<BaseMinion> minions = new HashSet<>();
	@Getter private Minions plugin;
	@Getter MinionConfig minionConfig;
	@Getter Set<MinionData> minionList;
	@Getter DataFile dataFile;
	@Getter Set<BaseMinion> loadedMinions = new HashSet<>();

	public MinionManager (Minions pl) {
		this.plugin = pl;
		this.minionConfig = new MinionConfig(this.plugin);
		minionConfig.reloadConfig();
		this.minionList = minionConfig.getAllMinionsInConfig();
		this.dataFile = new DataFile(this.plugin);
		dataFile.reloadConfig();
	}

	public void spawnAllMinions() {
		this.loadedMinions = dataFile.getPlayersMinions();
		for (BaseMinion minion: loadedMinions) {
			minion.spawnMinion();
		}
	}

	public void despawnAllMinions() {
		if (loadedMinions == null) return;
		dataFile.savePlayersMinions(loadedMinions);
		for (BaseMinion minion : loadedMinions) {
			minion.getMinion().remove();
		}
	}

	public void newMinionPlaced(BaseMinion minion) {
		this.loadedMinions.add(minion);
		dataFile.savePlayersMinions(loadedMinions);
	}
}
