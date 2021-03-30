package com.gonagutor.minions.configs;

import com.gonagutor.minions.Minions;
import com.gonagutor.minions.api.BaseMinion;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataFile {

	private Minions plugin;
	private JSONObject json;
	private JSONParser parser = new JSONParser();
	private File dataFile = null;

	public DataFile(Minions pl) {
		this.plugin = pl;
		saveDefaultConfig();
	}

	/**
	 * Reloads the config json file
	 */
	public void reloadConfig() {
		if (this.dataFile == null) this.dataFile =
			new File(this.plugin.getDataFolder(), "data.json");
		try {
			this.json =
				(JSONObject) parser.parse(
					new InputStreamReader(
						new FileInputStream(this.dataFile),
						"UTF-8"
					)
				);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the json object containing the player minions
	 *
	 * @return JSONObject containing the minions
	 */
	public JSONObject getConfig() {
		if (this.json == null) reloadConfig();
		return this.json;
	}

	/**
	 * Saves the json file. Pretty printed
	 */
	public void saveConfig() {
		if (this.json == null || this.dataFile == null) return;
		try {
			Gson g = new GsonBuilder().setPrettyPrinting().create();
			FileWriter fw = new FileWriter(this.dataFile);
			fw.write(g.toJson(json));
			fw.flush();
			fw.close();
		} catch (IOException e) {
			plugin
				.getLogger()
				.log(
					Level.SEVERE,
					Minions.getPrefix() +
					"File could not be saved in: " +
					this.dataFile,
					e
				);
		}
	}

	/**
	 * Saves the file from the plugin resources if the file is not found in the
	 * config folder
	 */
	public void saveDefaultConfig() {
		if (this.dataFile == null) this.dataFile =
			new File(this.plugin.getDataFolder(), "data.json");
		if (!this.dataFile.exists()) {
			this.plugin.saveResource("data.json", false);
		}
	}

	/**
	 * Recovers the config
	 *
	 * @return Set containing player minions
	 */
	@SuppressWarnings("unchecked")
	public Set<BaseMinion> getPlayersMinions() {
		Set<BaseMinion> minions = new HashSet<>();
		JSONArray array = (JSONArray) this.getConfig().get("player_minions");
		for (int i = 0; i < array.size(); i++) {
			BaseMinion minion = BaseMinion.deserialize(
				(Map<String, Object>) array.get(i)
			);
			minions.add(minion);
		}

		return minions;
	}

	/**
	 * Saves player minions to the json file
	 *
	 * @param minions Set containing player's minions
	 */
	@SuppressWarnings("unchecked")
	public void savePlayersMinions(Set<BaseMinion> minions) {
		JSONArray array = new JSONArray();

		for (BaseMinion minion : minions) {
			array.add(minion.serialize());
		}
		this.getConfig().put("player_minions", array);
		this.saveConfig();
	}
}
