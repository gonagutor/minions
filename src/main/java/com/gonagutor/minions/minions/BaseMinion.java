package com.gonagutor.minions.minions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.configs.MinionData;
import com.google.common.collect.ImmutableMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import lombok.Getter;
import lombok.Setter;

public class BaseMinion implements ConfigurationSerializable {
	@Getter @Setter private ArmorStand minion;
	@Getter private Location minionLocation;

	@Getter @Setter private MinionData minionData;

	@Getter @Setter private int level;
	@Getter @Setter private String menuTitle;
	@Getter @Setter private BukkitTask minionTask;
	@Getter @Setter private int items;
	@Getter @Setter private long money;
	@Getter @Setter private UUID playerUuid;
	public BaseMinion (Location newMinionLoc) {
		this.minionLocation = newMinionLoc;
	}

	private BaseMinion (Location loc, MinionData mData, int level, int ite, long mon, UUID player) {
		this.setMenuTitle(mData.getMinionName() + " level " + level);
		this.setMinionData(mData);
		this.minionLocation = loc;
		this.setLevel(level);
		this.setItems(ite);
		this.setMoney(mon);
		this.setPlayerUuid(player);
		spawnMinion();
	}

	public void spawnMinion() {
		this.minion = (ArmorStand) minionLocation.getWorld().spawnEntity(minionLocation, EntityType.ARMOR_STAND);
		this.minion.setSmall(true);
		this.minion.setInvulnerable(true);
		this.minion.setGravity(false);
		this.minion.setArms(true);
		this.minion.addEquipmentLock(EquipmentSlot.HAND, LockType.REMOVING_OR_CHANGING);
		this.minion.addEquipmentLock(EquipmentSlot.HEAD, LockType.REMOVING_OR_CHANGING);
		this.minion.addEquipmentLock(EquipmentSlot.CHEST, LockType.REMOVING_OR_CHANGING);
		this.minion.addEquipmentLock(EquipmentSlot.LEGS, LockType.REMOVING_OR_CHANGING);
		this.minion.addEquipmentLock(EquipmentSlot.FEET, LockType.REMOVING_OR_CHANGING);
		minion.getEquipment().setHelmet(minionData.toSkull());

		if (this.getMinionData().getChest() != null)
			minion.getEquipment().setChestplate(this.getMinionData().getChest());
		if (this.getMinionData().getLegs() != null)
			minion.getEquipment().setLeggings(this.getMinionData().getLegs());
		if (this.getMinionData().getBoots() != null)
			minion.getEquipment().setBoots(this.getMinionData().getBoots());
		if (this.getMinionData().getTool() != null)
			minion.getEquipment().setItemInMainHand(this.getMinionData().getTool());
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
		BukkitTask animation = new AnimateArm(target).runTaskTimer(Minions.getPlugin(Minions.class), 0, 1);
		Bukkit.getScheduler().runTaskLater(Minions.getPlugin(Minions.class), () -> {
			animation.cancel();
			target.setLeftArmPose(new EulerAngle(0, 0, 0));
		} , 40 * (10 / this.getLevel()));
	}

//		** Serialization **
	@SuppressWarnings("unchecked")
	public static BaseMinion deserialize(Map<String, Object> map) {
		try {
			return new BaseMinion(
				Location.deserialize((Map<String, Object>) map.get("minion_location")),
				MinionData.deserialize((Map<String, Object>) map.get("minion_data")), //TODO: Implement a way to not save the whole minion
				((Number) map.get("level")).intValue(),
				((Number) map.get("items")).intValue(),
				(long) map.get("money"),
				UUID.fromString((String) map.get("player_uuid"))
			);
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage("ERRRRROR");
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, Object> serialize() {
		return ImmutableMap.<String, Object>builder()
			.put("minion_location", minionLocation.serialize())
			.put("minion_data", minionData.serialize())
			.put("level", level)
			.put("items", items)
			.put("money", (long) money)
			.put("player_uuid", playerUuid.toString())
			.build();
	}
}
