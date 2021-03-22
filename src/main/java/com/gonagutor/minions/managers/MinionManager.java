package com.gonagutor.minions.managers;

import java.util.HashSet;
import java.util.Set;

import com.gonagutor.minions.minions.BaseMinion;

import org.bukkit.plugin.Plugin;

import lombok.Getter;
import lombok.Setter;

public class MinionManager {
	@Getter @Setter private Set<BaseMinion> minions = new HashSet<>();
	@Getter private Plugin plugin;

	public MinionManager (Plugin pl) {
		this.plugin = pl;
	}
}
