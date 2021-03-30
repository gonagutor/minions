package com.gonagutor.minions.api;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.configs.JSONMinionData;
import com.gonagutor.minions.configs.MinionData;
import com.google.common.collect.ImmutableMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public abstract class BaseMinion {

	@Getter
	@Setter
	private ArmorStand minion;

	@Getter
	private Location minionLocation;

	@Getter
	@Setter
	private MinionData minionData;

	@Getter
	@Setter
	private String identifier;

	@Getter
	@Setter
	private int level;

	@Getter
	@Setter
	private String menuTitle;

	@Getter
	@Setter
	private BukkitTask minionTask;

	@Getter
	@Setter
	private int items;

	@Getter
	@Setter
	private long money;

	@Getter
	@Setter
	private UUID playerUuid;

	/**
	 * This must be super(ed) by any minion extension
	 *
	 * @param newMinionLoc
	 */
	public BaseMinion(
		Location loc,
		MinionData mData,
		int level,
		int ite,
		long mon,
		UUID player
	) {
		this.setIdentifier(this.getClass().getName());
		this.setMenuTitle(mData.getMinionName() + " level " + level);
		this.setMinionData(mData);
		this.minionLocation = loc;
		this.setLevel(level);
		this.setItems(ite);
		this.setMoney(mon);
		this.setPlayerUuid(player);
	}

	/**
	 * Spawns the minion, applies locks to slots, sets characteristics and adds
	 * equipment to entity, including custom skull.
	 */
	public void spawnMinion() {
		this.minion =
			(ArmorStand) minionLocation
				.getWorld()
				.spawnEntity(minionLocation, EntityType.ARMOR_STAND);
		this.minion.setSmall(true);
		this.minion.setInvulnerable(true);
		this.minion.setGravity(false);
		this.minion.setArms(true);
		this.minion.setBasePlate(false);
		this.minion.addEquipmentLock(
				EquipmentSlot.HAND,
				LockType.REMOVING_OR_CHANGING
			);
		this.minion.addEquipmentLock(
				EquipmentSlot.HEAD,
				LockType.REMOVING_OR_CHANGING
			);
		this.minion.addEquipmentLock(
				EquipmentSlot.CHEST,
				LockType.REMOVING_OR_CHANGING
			);
		this.minion.addEquipmentLock(
				EquipmentSlot.LEGS,
				LockType.REMOVING_OR_CHANGING
			);
		this.minion.addEquipmentLock(
				EquipmentSlot.FEET,
				LockType.REMOVING_OR_CHANGING
			);
		minion.getEquipment().setHelmet(minionData.toSkull());

		if (this.getMinionData().getChest() != null) minion
			.getEquipment()
			.setChestplate(this.getMinionData().getChest());
		if (this.getMinionData().getLegs() != null) minion
			.getEquipment()
			.setLeggings(this.getMinionData().getLegs());
		if (this.getMinionData().getBoots() != null) minion
			.getEquipment()
			.setBoots(this.getMinionData().getBoots());
		if (this.getMinionData().getTool() != null) minion
			.getEquipment()
			.setItemInMainHand(this.getMinionData().getTool());
	}

	/**
	 * Get the minion's influence blocks
	 *
	 * @return Set containing including blocks
	 */
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
				)
					.getBlock();
				blocks.add(block);
			}
			blocks.remove(
				new Location(
					minionLocation.getWorld(),
					minionLocation.getX(),
					minionLocation.getY() - 1,
					minionLocation.getZ()
				)
					.getBlock()
			);
		}
		return (blocks);
	}

	/* -------------- Animation Utilities -------------- */
	/* This section contains minion animation utilities */

	/**
	 * Restores ArmourStand pose abruptly with no animation
	 */
	public void restorePose() {
		EulerAngle zeroRot = new EulerAngle(0, 0, 0);
		this.minion.setHeadPose(zeroRot);
		this.minion.setRightArmPose(zeroRot);
		this.minion.setLeftArmPose(zeroRot);
		this.minion.setLeftLegPose(zeroRot);
		this.minion.setRightLegPose(zeroRot);
		this.minion.setBodyPose(zeroRot);
	}

	/**
	 * Rotates minion to face location
	 *
	 * @param lookHere Location to look towards
	 */
	public void rotateMinionToLocation(Location lookHere) {
		ArmorStand target = this.getMinion();
		Vector lookDirection = target.getLocation().toVector().clone();
		lookDirection.subtract(lookHere.toVector());

		Vector direction = target
			.getLocation()
			.toVector()
			.subtract(lookHere.add(0.5, 0.5, 0.5).toVector())
			.normalize();
		double x = direction.getX();
		double y = direction.getY();
		double z = direction.getZ();

		Location changed = target.getLocation().clone();
		changed.setYaw(180 - (float) Math.toDegrees(Math.atan2(x, z)));
		changed.setPitch(90 - (float) Math.toDegrees(Math.acos(y)));

		target.setHeadPose(
			new EulerAngle(Math.asin(1 / lookDirection.length()), 0, 0)
		);
		target.teleport(changed);
	}

	/**
	 * Plays out block break hand animation
	 */
	public void playOutBreakAnimation() {
		ArmorStand target = this.getMinion();
		final class AnimateBreak extends BukkitRunnable {

			ArmorStand armorStand;
			Boolean forward = true;

			public AnimateBreak(ArmorStand as) {
				this.armorStand = as;
			}

			@Override
			public void run() {
				if (armorStand.getRightArmPose().getX() < -0.5) forward = false;
				if (armorStand.getRightArmPose().getX() > -0.1) forward = true;

				if (forward) armorStand.setRightArmPose(
					armorStand.getRightArmPose().subtract(0.1, 0, 0)
				); else armorStand.setRightArmPose(
					armorStand.getRightArmPose().add(0.1, 0, 0)
				);
			}
		}
		BukkitTask animation = new AnimateBreak(target)
		.runTaskTimer(Minions.getPlugin(Minions.class), 0, 1);
		Bukkit
			.getScheduler()
			.runTaskLater(
				Minions.getPlugin(Minions.class),
				() -> {
					animation.cancel();
					this.restorePose();
				},
				20
			);
	}

	/**
	 * Plays out block place hand animation
	 */
	public void playOutPlaceAnimation() {
		this.getMinion().setRightArmPose(new EulerAngle(-3, 0, 0));
		this.getMinion().setLeftArmPose(new EulerAngle(-3, 0, 0));
		final class AnimatePlace extends BukkitRunnable {

			ArmorStand armorStand;

			public AnimatePlace(ArmorStand as) {
				this.armorStand = as;
			}

			@Override
			public void run() {
				if (armorStand.getRightArmPose().getX() < 0) {
					armorStand.setRightArmPose(
						armorStand.getRightArmPose().add(0.15, 0, 0)
					);
				}
			}
		}
		BukkitTask animation = new AnimatePlace(this.getMinion())
		.runTaskTimer(Minions.getPlugin(Minions.class), 0, 1);
		Bukkit
			.getScheduler()
			.runTaskLater(
				Minions.getPlugin(Minions.class),
				() -> {
					animation.cancel();
					this.restorePose();
				},
				20
			);
	}

	/* ----------- Serialization Utilities ----------- */
	/* This section contains methods for serialization */

	/**
	 * Deserialize a map to get the minion data.
	 * <p>
	 * <b>This method will return null if it deserialize fails</b>
	 * </p>
	 *
	 * @param map String -> Object map containing minion data
	 * @return New BaseMinion from map data.
	 */
	@SuppressWarnings("unchecked")
	public static BaseMinion deserialize(Map<String, Object> map) {
		try {
			return (BaseMinion) Class
				.forName((String) map.get("minion_type"))
				.getConstructor(
					Location.class,
					MinionData.class,
					int.class,
					int.class,
					long.class,
					UUID.class
				)
				.newInstance(
					Location.deserialize(
						(Map<String, Object>) map.get("minion_location")
					),
					JSONMinionData.deserialize((String) map.get("minion_data")),
					((Number) map.get("level")).intValue(),
					((Number) map.get("items")).intValue(),
					(long) map.get("money"),
					UUID.fromString((String) map.get("player_uuid"))
				);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Serializes this minion to a map
	 */
	public Map<String, Object> serialize() {
		return ImmutableMap
			.<String, Object>builder()
			.put("minion_location", minionLocation.serialize())
			.put("minion_type", identifier)
			.put("minion_data", JSONMinionData.serialize(minionData))
			.put("level", level)
			.put("items", items)
			.put("money", (long) money)
			.put("player_uuid", playerUuid.toString())
			.build();
	}
}
