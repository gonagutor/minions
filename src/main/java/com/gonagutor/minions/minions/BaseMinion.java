package com.gonagutor.minions.minions;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

abstract class BaseMinion {
	@Getter @Setter private ArmorStand minion;
	@Getter private Location minionLocation;

	@Getter @Setter private ItemStack head;
	@Getter @Setter private ItemStack chest;
	@Getter @Setter private ItemStack legs;
	@Getter @Setter private ItemStack boots;
	@Getter @Setter private ItemStack tool;

	@Getter @Setter private int level;
	public BaseMinion (Location newMinionLoc) {
		this.minionLocation = newMinionLoc;
	}

	public void spawnMinion() {
		this.minion = (ArmorStand) minionLocation.getWorld().spawnEntity(minionLocation, EntityType.ARMOR_STAND);
		this.minion.setSmall(true);
		this.minion.setInvulnerable(true);
		this.minion.setGravity(false);
		if (head != null)
			minion.getEquipment().setHelmet(head);
		if (chest != null)
			minion.getEquipment().setChestplate(chest);
		if (legs != null)
			minion.getEquipment().setLeggings(legs);
		if (boots != null)
			minion.getEquipment().setBoots(boots);
		if (tool != null)
			minion.getEquipment().setItemInMainHand(tool);
		this.minion.addEquipmentLock(EquipmentSlot.HAND, LockType.REMOVING_OR_CHANGING);
		this.minion.addEquipmentLock(EquipmentSlot.HEAD, LockType.REMOVING_OR_CHANGING);
		this.minion.addEquipmentLock(EquipmentSlot.CHEST, LockType.REMOVING_OR_CHANGING);
		this.minion.addEquipmentLock(EquipmentSlot.LEGS, LockType.REMOVING_OR_CHANGING);
		this.minion.addEquipmentLock(EquipmentSlot.FEET, LockType.REMOVING_OR_CHANGING);
	}

	public Set<Block> getInfluenceBlocks() {
		int minionRadius = 2;
		Set<Block> blocks = new HashSet<>();
        for (float sqx = 0; sqx < minionRadius * 2; sqx++) {
            float x = minionRadius - sqx;
            for (float sqz = 0; sqz < minionRadius * 2; sqz++) {
                float z = minionRadius - sqz;
                Block block = new Location(
                    minionLocation.getWorld(),
                    minionLocation.getX() + x,
                    minionLocation.getY() - 1,
                    minionLocation.getZ() + z
                ).getBlock();
                blocks.add(block);
            }
			blocks.remove(new Location(
				minionLocation.getWorld(),
				minionLocation.getX(),
				minionLocation.getY() - 1,
				minionLocation.getZ()
			).getBlock());
        }
        return (blocks);
	}
}
