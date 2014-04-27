/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xtrsource.minetp;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author Thomas
 */
public class MineTP extends JavaPlugin {

	public ConfigUtil			config;
	public DatabaseUtil			database;
	private static final Logger	logger	= Logger.getLogger("minecraft");

	@Override
	public void onEnable() {
		loadConfig();
		MineTP.log("[MineTP] Plugin by " + this.getDescription().getAuthors());

		// Metrics Plugin
		if (getConfig().getBoolean("config.allowpluginmetrics")) {
			try {
				Metrics metrics = new Metrics(this);
				metrics.start();
				MineTP.log("[MineTP] PluginMetrics enabled.");
			} catch (Exception e) {
				MineTP.log("[MineTP] Failed to activate PluginMetrics.");
			}
		} else {
			MineTP.log("[MineTP] PluginMetrics disabled.");
		}
		// Metrics Plugin

		database = new DatabaseUtil(this);
		getServer().getPluginManager().registerEvents(new MineTPListener(this), this);
		
		this.registerCommand("mtp", new MtpCommand(this));
	}
	public void registerCommand(String command, CommandExecutor cmd) {
		try {
			this.getCommand(command).setExecutor(cmd);
		} catch (NullPointerException e) {
			MineTP.log(Level.WARNING, "Could not register command: "+command+" due to missing command in plugin.yml");
		}
	}

	public static void log(String msg) {
		logger.info((new StringBuilder("[SimpleWarnings] ")).append(msg).toString());
	}

	public static void log(Level level, String msg) {
		logger.log(level, (new StringBuilder("[SimpleWarnings] ")).append(msg).toString());
	}

	private void loadConfig() {
		this.getConfig().options().header("MINETP CONFIG");
		this.getConfig().addDefault("config.messages.teleportset", "User will be teleported the next time he is online.");
		this.getConfig().addDefault("config.messages.teleportself", "You cannot schedule a teleport for yourself");
		this.getConfig().addDefault("config.messages.gotteleported", "You got teleported by %teleporter_name%");
		this.getConfig().addDefault("config.messages.successremove", "Teleport successfully removed.");
		this.getConfig().addDefault("config.errormessages.nopermission", "You don't have the required permissons.");
		this.getConfig().addDefault("config.errormessages.usernamealreadyused", "A Teleport for this user is already set.");
		this.getConfig().addDefault("config.errormessages.syntaxerror", "Please check syntax! Usage:");
		this.getConfig().addDefault("config.errormessages.removefaild", "The teleport couldn't be removed.");
		this.getConfig().addDefault("config.update.message.check", "Check for updates ... ");
		this.getConfig().addDefault("config.update.message.newupdate", "A newer Version is available.");
		this.getConfig().addDefault("config.update.message.noupdate", "MineTP is up to date.");
		this.getConfig().addDefault("config.update.message.developementbuild", "You are using a developementbuild.");
		this.getConfig().addDefault("config.update.message.error", "Check for updates failed.");
		this.getConfig().addDefault("config.allowpluginmetrics", true);

		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}

	public boolean permCheck(Player player, String permission) {
		if (player.isOp() || player.hasPermission(permission)) {
			return true;
		}
		return false;
	}
	
	public DatabaseUtil getDatabaseUtil() {
		return this.database;
	}
}
