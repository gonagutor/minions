package com.gonagutor.minions.configs;

import java.lang.reflect.Field;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import org.bukkit.Material;

public class CustomSkull {
	private final static String FALLBACK = "DangerourMtp";
	// If there is a way to sign them using a web there must be a way
	// to implement reading an image from a file and encode it as mojang
	// does and use textures here. 
	// See https://github.com/MineSkin/mineskin.org/blob/master/script.js for signing info

	@SuppressWarnings("deprecation")
	public static ItemStack skullFromImage(ItemStack skull, String url) {
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		if (skull.getType() != Material.PLAYER_HEAD) {
			meta.setOwner(FALLBACK);
			return skull;
		}
		try {
			GameProfile newSkinProfile = new GameProfile(UUID.randomUUID(), null);
			Field profileField = null;

			newSkinProfile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + url + "\"}}}")));
			profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, newSkinProfile);
			skull.setItemMeta(meta);
			return skull;
		} catch (Exception e) {
			e.printStackTrace();
			meta.setOwner(FALLBACK);
			return skull;
		}
	}
}
