package com.gonagutor.minions.minions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockMinion extends BaseMinion {
	public BlockMinion(Location minionLoc) {
		super(minionLoc);
		this.getInfluenceBlocks().forEach((Block block) -> {
			block.setType(Material.GREEN_STAINED_GLASS);
		});
	}
}
