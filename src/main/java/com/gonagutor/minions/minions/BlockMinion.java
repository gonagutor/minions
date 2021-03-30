package com.gonagutor.minions.minions;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.api.BaseMinion;
import com.gonagutor.minions.configs.MinionData;
import com.gonagutor.minions.nms.PacketSender;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class BlockMinion extends BaseMinion {

	@Getter
	@Setter
	private Material blockType;

	/**
	 * This BukkitRunnable will be instantiated when the minion need to break a
	 * block
	 *
	 * TODO: Refine block break period. Current period is 20 seconds reduced by two
	 * by each upgrade Current period feels too fast at level 10 and too slow at
	 * lower levels
	 */
	private class UpdateNeeded extends BukkitRunnable {

		private BlockMinion minion;

		public UpdateNeeded(BlockMinion mnn) {
			this.minion = mnn;
		}

		@Override
		public void run() {
			if (!minion.getMinion().getLocation().getChunk().isLoaded()) return;
			Set<Block> influenceBlocks = minion.getInfluenceBlocks();
			for (Block block : influenceBlocks) {
				if (block.getType() == Material.AIR) {
					block.setType(minion.getBlockType());
					minion.rotateMinionToLocation(block.getLocation());
					minion.playOutPlaceAnimation();
					return;
				}
			}
			if (minion.getItems() < 64 * 3 * 5) {
				Block b = (Block) influenceBlocks.toArray()[(int) Math.floor(
						influenceBlocks.size() * Math.random()
					)];
				minion.playOutBreakAnimation();
				minion.rotateMinionToLocation(b.getLocation());
				new BukkitRunnable() {
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
				}
				.runTaskTimer(Minions.getPlugin(Minions.class), 0, 2);
			}
		}
	}

	/**
	 * This constructor is required for deserialize
	 *
	 * @param loc    ArmourStand entity location
	 * @param mData  MinionData from config
	 * @param level  Minion level
	 * @param ite    Minion items quantity
	 * @param mon    Minion money
	 * @param player UUID of the player who placed the minion
	 */
	public BlockMinion(
		Location loc,
		MinionData mData,
		int level,
		int ite,
		long mon,
		UUID player
	) {
		super(loc, mData, level, ite, mon, player);
		this.blockType = mData.getBlockMaterial();
		this.setMinionTask(
				new UpdateNeeded(this)
				.runTaskTimer(
						Minions.getPlugin(Minions.class),
						40,
						40 * (10 / this.getLevel())
					)
			);
	}
}
