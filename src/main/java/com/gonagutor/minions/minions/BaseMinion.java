package com.gonagutor.minions.minions;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;

public class BaseMinion{
	@Getter @Setter private ArmorStand minion;
	@Getter private Location minionLocation;

	@Getter @Setter private ItemStack head;
	@Getter @Setter private ItemStack chest;
	@Getter @Setter private ItemStack legs;
	@Getter @Setter private ItemStack boots;
	@Getter @Setter private ItemStack tool;

	@Getter @Setter private int level;
	@Getter @Setter private Color color;
	@Getter @Setter private String menuTitle;
	@Getter @Setter private BukkitTask minionTask;
	@Getter @Setter private Material material;
	@Getter @Setter private int items;
	@Getter @Setter private long money;
	public BaseMinion (Location newMinionLoc) {
		this.minionLocation = newMinionLoc;
	}

	public void spawnMinion() {
		this.minion = (ArmorStand) minionLocation.getWorld().spawnEntity(minionLocation, EntityType.ARMOR_STAND);
		this.minion.setSmall(true);
		this.minion.setInvulnerable(true);
		this.minion.setGravity(false);
		this.minion.setArms(true);
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
        for (float sqx = 0; sqx < minionRadius * 2 + 1; sqx++) {
            float x = minionRadius - sqx;
            for (float sqz = 0; sqz < minionRadius * 2 + 1; sqz++) {
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

	public void rotateMinionToLocation(Location lookHere) {
		ArmorStand target = this.getMinion();

		Vector direction = target.getLocation().toVector().subtract(lookHere.add(0.5, 0.5, 0.5).toVector()) .normalize();
		double x = direction.getX();
		double y = direction.getY();
		double z = direction.getZ();

		Location changed = target.getLocation().clone();
		changed.setYaw(180 - (float) Math.toDegrees(Math.atan2(x, z)));
		changed.setPitch(90 - (float) Math.toDegrees(Math.acos(y)));
		target.teleport(changed);
	}

	public void playOutAnimation() {
		ArmorStand target = this.getMinion();
		final class AnimateArm extends BukkitRunnable {
			ArmorStand armorStand;
			Boolean forward = true;
			public AnimateArm (ArmorStand as) {
				this.armorStand = as;
			}

			@Override
			public void run() {
				if (armorStand.getRightArmPose().getX() < -0.5)
					forward = false;
				if (armorStand.getRightArmPose().getX() > -0.1)
					forward = true;

				if (forward)
					armorStand.setRightArmPose(armorStand.getRightArmPose().subtract(0.1, 0, 0));
				else
					armorStand.setRightArmPose(armorStand.getRightArmPose().add(0.1, 0, 0));
			}
		}
		BukkitTask animation = new AnimateArm(target).runTaskTimer(Bukkit.getPluginManager().getPlugin("Minions"), 0, 1);
		Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Minions"), () -> {
			animation.cancel();
			target.setLeftArmPose(new EulerAngle(0, 0, 0));
		} , 40 * (10 / this.getLevel()));
	}
}
