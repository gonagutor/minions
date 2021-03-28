package com.gonagutor.minions.minions;

import java.util.Set;
import java.util.UUID;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.configs.MinionData;
import com.gonagutor.minions.nms.PacketSender;

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
					block.setType(minion.getBlockType());
					minion.rotateMinionToLocation(block.getLocation());
					minion.playOutPlaceAnimation();
					return;
				}
			}
			if (minion.getItems() < 64 * 3 * 5) {
				Block b = (Block)inflBlocks.toArray()[(int)Math.floor(inflBlocks.size() * Math.random())];
				minion.playOutBreakAnimation();
				minion.rotateMinionToLocation(b.getLocation());
				new BukkitRunnable(){
					PacketSender packet = new PacketSender();
					int status = 0;
					@Override
					public void run() {
						if (b.getType() == Material.AIR) {
							cancel();
							return;
						}
						if (status < 10) {
							packet.sendPacket(b.getLocation(), status);
							status++;
							return;
						}
						packet.sendPacket(b.getLocation(), status);
						b.setType(Material.AIR);
						minion.setItems(minion.getItems() + 1);
						this.cancel();
						return;
					}
				}.runTaskTimer(Minions.getPlugin(Minions.class), 0, 2);
			}
		}
	}

	public BlockMinion(Location minionLoc, MinionData minionData, int level, UUID player) {
		super(minionLoc);
		this.setMinionData(minionData);
		this.setPlayerUuid(player);
		this.setLevel(level);
		this.setMenuTitle(minionData.getMinionName() + " level " + level);
		this.blockType = minionData.getBlockMaterial();
		this.setMinionTask(new UpdateNeeded(this).runTaskTimer(
			Minions.getPlugin(Minions.class),
			40,
			40 * (10 / this.getLevel())
		));
	}

	public BlockMinion(BaseMinion minion) {
		super(minion.getMinionLocation());
		this.setMinionData(minion.getMinionData());
		this.setPlayerUuid(minion.getPlayerUuid());
		this.setItems(minion.getItems());
		this.setLevel(minion.getLevel());
		this.setMenuTitle(minion.getMinionData().getMinionName() + " level " + minion.getLevel());
		this.blockType = minion.getMinionData().getBlockMaterial();
		this.setMinionTask(new UpdateNeeded(this).runTaskTimer(
			Minions.getPlugin(Minions.class),
			40,
			40 * (10 / this.getLevel())
		));
	}
}
