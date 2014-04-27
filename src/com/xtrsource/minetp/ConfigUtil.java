/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xtrsource.minetp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * 
 * @author Thomas
 */
public class ConfigUtil {

	MineTP	plugin;

	public ConfigUtil(MineTP plugin) {
		this.plugin = plugin;

		loadConfig();
	}

	private void loadConfig() {
		System.out.println(plugin.getDataFolder());
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

		if (!configFile.exists()) {
			System.out.println("Starting generating config file");

			config.options().header("MINETP Config");
			config.set("test.path2", "TEST2");
			config.set("test.path2.path3", "TEST1");

			try {
				config.save(configFile);
			} catch (IOException ex) {
				Logger.getLogger(ConfigUtil.class.getName()).log(Level.SEVERE, null, ex);
			}

			System.out.println("Finished generating config file");
		}
	}
}
