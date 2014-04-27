package com.xtrsource.minetp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class MtpCommand implements CommandExecutor {
	
	public MineTP instance;
	
	public MtpCommand(MineTP instance) {
		this.instance = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("mtp")) {
			if (args.length == 0) {
				PluginDescriptionFile descFile = instance.getDescription();
				sender.sendMessage(ChatColor.GREEN + "-----------------------------------------------------");
				sender.sendMessage(ChatColor.GREEN + descFile.getFullName() + " by " + descFile.getAuthors());
				sender.sendMessage(ChatColor.GREEN + "Type /mtp help for help");
				sender.sendMessage(ChatColor.GREEN + "Type /mtp perms for permissions");
				sender.sendMessage(ChatColor.GREEN + "-----------------------------------------------------");
				return true;
			}

			if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
				if (sender instanceof Player) {
					if (!instance.permCheck((Player) sender, "minetp.teleportremove")) {
						sender.sendMessage(ChatColor.RED + instance.getConfig().getString("config.errormessages.nopermission"));
						return true;
					}
					if (instance.getDatabaseUtil().removeTeleport(args[1])) {
						sender.sendMessage(ChatColor.GREEN + instance.getConfig().getString("config.messages.successremove"));
					} else {
						sender.sendMessage(ChatColor.RED + instance.getConfig().getString("config.errormessages.removefaild"));
					}
					return true;
				}
			}

			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + instance.getConfig().getString("config.errormessages.syntaxerror") + "/mtp <player>");
				return true;
			}
			if (args[0].equalsIgnoreCase("perms")) {
				sender.sendMessage(ChatColor.GREEN + "minetp.teleportoffline: allows to plan a teleport");
				sender.sendMessage(ChatColor.GREEN + "minetp.teleportremove: alloes to remove a planed teleport");
				return true;
			}
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.GREEN + "Type /mtp <player> to teleport the player to your position after join.");
				sender.sendMessage(ChatColor.GREEN + "Type /mtp remove <player>  to remove a planned teleport");
				return true;
			}

			if (sender instanceof Player) {
				if (!instance.permCheck((Player) sender, "minetp.teleportoffline")) {
					sender.sendMessage(ChatColor.RED + instance.getConfig().getString("config.errormessages.nopermission"));
					return true;
				}

				if (instance.getDatabaseUtil().firstData(args[0])) {
					instance.getDatabaseUtil().addData(args[0], sender.getName(), instance.getServer().getPlayer(sender.getName()).getLocation());
					sender.sendMessage(ChatColor.GREEN + instance.getConfig().getString("config.messages.teleportset"));
				} else {
					sender.sendMessage(ChatColor.RED + instance.getConfig().getString("config.errormessages.usernamealreadyused"));
				}
			}
		}
		return true;
	}
}
