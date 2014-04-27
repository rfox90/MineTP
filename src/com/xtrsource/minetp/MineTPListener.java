/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xtrsource.minetp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;

/**
 * 
 * @author Thomas
 */
class MineTPListener implements Listener {

	private MineTP	plugin;

	public MineTPListener(MineTP aThis) {
		plugin = aThis;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if(p!=null) {
			String teleporter = plugin.getDatabaseUtil().getTeleporterName(p.getName());
			Location l = plugin.getDatabaseUtil().getLocation(p);
			if (l != null) {
				p.teleport(l);
				p.getPlayer().sendMessage(ChatColor.YELLOW + plugin.getConfig().getString("config.messages.gotteleported").replace("%teleporter_name%", teleporter));
				plugin.getDatabaseUtil().deleteTeleport(p);
			}
		}
	}

	@EventHandler
	public void checkUpdate(PlayerJoinEvent event) {
		if (plugin.permCheck(event.getPlayer(), "minetp.checkupdate")) {
			checkVersion(event.getPlayer());
		}

	}

	private void checkVersion(Player player) {

		player.sendMessage(ChatColor.GREEN + "[" + plugin.getDescription().getName() + "] " + plugin.getConfig().getString("config.update.message.check"));
		MineTP.log("[" + plugin.getDescription().getName() + "] Check for updates ...");

		PluginDescriptionFile descFile = plugin.getDescription();
		URL url = null;
		BufferedInputStream bufferedInput = null;
		byte[] buffer = new byte[1024];
		try {
			url = new URL("http://ahref.co.uk/minecraft/bukkit/minetp/VERSION");
		} catch (MalformedURLException ex) {
			MineTP.log("[" + plugin.getDescription().getName() + "] Check for updates failed.");
			player.sendMessage(ChatColor.RED + "[MineTP] " + plugin.getConfig().getString("config.update.message.error"));
		}
		try {
			bufferedInput = new BufferedInputStream(url.openStream());
			int bytesRead = 0;
			while ((bytesRead = bufferedInput.read(buffer)) != -1) {

				String version = new String(buffer, 0, bytesRead);
				if (Float.valueOf(version) > Float.valueOf(descFile.getVersion())) {
					player.sendMessage(ChatColor.GOLD + "[" + plugin.getDescription().getName() + "] " + plugin.getConfig().getString("config.update.message.newupdate"));
					MineTP.log("[" + plugin.getDescription().getName() + "] A newer Version is available.");
				} else {
					if (version.equals(descFile.getVersion())) {
						player.sendMessage(ChatColor.GREEN + "[" + plugin.getDescription().getName() + "] " + plugin.getConfig().getString("config.update.message.noupdate"));
						MineTP.log("[" + plugin.getDescription().getName() + "] " + plugin.getDescription().getName() + " is up to date.");
					} else {
						player.sendMessage(ChatColor.RED + "[" + plugin.getDescription().getName() + "] " + plugin.getConfig().getString("config.update.message.developementbuild"));
						MineTP.log("[" + plugin.getDescription().getName() + "] You are using a developementbuild.");
					}
				}
			}
			bufferedInput.close();

		} catch (IOException ex) {
			MineTP.log("["+plugin.getDescription().getName()+"] Check for updates failed!");
			player.sendMessage(ChatColor.RED + "["+plugin.getDescription().getName()+"]"+ plugin.getConfig().getString("config.update.message.error"));
		}
	}

}
