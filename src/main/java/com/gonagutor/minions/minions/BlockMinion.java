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
			if (!minion.getMinion().getLocation().getChunk().isLoaded()) return;
			Set<Block> inflBlocks = minion.getInfluenceBlocks();
			for (Block block : inflBlocks) {
				if (block.getType() == Material.AIR) {
					minion.playOutAnimation();
					minion.rotateMinionToLocation(block.getLocation());
					block.setType(minion.getBlockType());
					return;
				}
			}
			if (minion.getItems() < 64 * 3 * 5) {
				Block b = (Block)inflBlocks.toArray()[(int)Math.floor(inflBlocks.size() * Math.random())];
				minion.rotateMinionToLocation(b.getLocation());
				b.setType(Material.AIR);
				minion.setItems(minion.getItems() + 1);
			}
		}
	}

	public BlockMinion(Location minionLoc, Material block, Material drop,int level, String minionKind) {
		super(minionLoc);
		this.setLevel(level);
		this.setMenuTitle(minionKind + " level " + level);
		this.blockType = block;
		this.setMaterial(drop);
		this.setMinionTask(new UpdateNeeded(this).runTaskTimer(
			Bukkit.getPluginManager().getPlugin("Minions"),
			40,
			40 * (10 / this.getLevel())
		));
	}
}
