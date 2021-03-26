package com.gonagutor.minions.nms;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PacketSender {

	/**
	 * Requests a class from the net.minecraft.server depending on server version
	 * @param className The class to request
	 * @return Requested class from net.minecraft.server
	 */
	private Class<?> getNMSClass (String className) {
		try {
			return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackageName().split("\\.")[3] + "." + className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object buildPacket(Location block, int state) {
		try {
			Constructor<?> blockConstructor = getNMSClass("BlockPosition").getConstructor(int.class, int.class, int.class);
			Object blockPostion = blockConstructor.newInstance(block.getBlockX(), block.getBlockY(), block.getBlockZ());
			Constructor<?> packetConstructor = getNMSClass("PacketPlayOutBlockBreakAnimation").getConstructor(int.class, getNMSClass("BlockPosition"), int.class);
			Object packet = packetConstructor.newInstance((int) Math.random() * 1000, blockPostion, state);
			return packet;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sends the packet of the block break animation to all players.
	 * TODO: Send only to close players
	 * @param block Location in which to play the packet
	 * @param state	(0-6) Current block break state
	 */
	public void sendPacket(Location block, int state) {
		try {
			Object packet = buildPacket(block, state);
			for (Player p : Bukkit.getOnlinePlayers()) {
				Object handle = p.getClass().getMethod("getHandle").invoke(p);
				Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
				playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
