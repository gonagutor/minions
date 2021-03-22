package com.gonagutor.minions.minions;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import lombok.Setter;

public class BlockMinion extends BaseMinion {
	@Getter @Setter private Material blockType;
	private class UpdateNeeded extends BukkitRunnable {
		private BlockMinion minion;
		public UpdateNeeded(BlockMinion mnn) { this.minion = mnn; }

		@Override
		public void run() {
			Set<Block> inflBlocks = minion.getInfluenceBlocks();
			for (Block block : inflBlocks) {
				if (block.getType() == Material.AIR) {
					block.setType(minion.getBlockType());
					return;
				}
			}
			Block b = (Block)inflBlocks.toArray()[(int)Math.floor(inflBlocks.size() * Math.random())];
			b.setType(Material.AIR);
		}
	}

	public BlockMinion(Location minionLoc, Material block, int level) {
		super(minionLoc);
		this.setLevel(level);
		this.blockType = block;
		this.setMinionTask(new UpdateNeeded(this).runTaskTimer(
			Bukkit.getPluginManager().getPlugin("Minions"),
			40,
			40 * (10 / this.getLevel())
		));
	}
}
