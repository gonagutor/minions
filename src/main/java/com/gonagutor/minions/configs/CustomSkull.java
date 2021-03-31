package com.gonagutor.minions.configs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class CustomSkull {

	private static final String FALLBACK = "DangerourMtp";

	// If there is a way to sign them using a web there must be a way
	// to implement reading an image from a file and encode it as mojang
	// does and use texture files here.
	// See https://github.com/MineSkin/mineskin.org/blob/master/script.js for
	// signing info

	/**
	 * Returns a custom skull from an url. This can be a fake player skin
	 *
	 * @param skull Skull to apply the texture to
	 * @param url   Skin image provider. Must be signed by mojang for now. Any
	 *              (...)textures.minecraft.net/texture/(...)
	 * @return Returns ItemStack with custom skin
	 */
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

			newSkinProfile.getProperties().put("textures",
				new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + url + "\"}}}"))
			);
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
