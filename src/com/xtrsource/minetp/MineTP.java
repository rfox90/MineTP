/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xtrsource.minetp;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Thomas
 */
public class MineTP extends JavaPlugin{
        
    public ConfigUtil config;
    public DatabaseUtil database;
    private static final Logger		logger	= Logger.getLogger("minecraft");
    
    @Override
    public void onEnable(){
        loadConfig();
        MineTP.log("[MineTP] Plugin by "+this.getDescription().getAuthors());
        
        // Metrics Plugin
        if (getConfig().getBoolean("config.allowpluginmetrics")){
            try {
               Metrics metrics = new Metrics(this);
               metrics.start();
               MineTP.log("[MineTP] PluginMetrics enabled.");
            } catch (Exception e) {
                MineTP.log("[MineTP] Failed to activate PluginMetrics.");
            }
        }
        else {
            MineTP.log("[MineTP] PluginMetrics disabled.");
        }
        //Metrics Plugin
        
        database = new DatabaseUtil(this);
        getServer().getPluginManager().registerEvents(new MineTPListener(this), this);    
    }
    public static void log(String msg) {
		logger.info((new StringBuilder("[SimpleWarnings] ")).append(msg).toString());
	}

	public static void log(Level level, String msg) {
		logger.log(level, (new StringBuilder("[SimpleWarnings] ")).append(msg).toString());
	}
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        
        if(cmd.getName().equalsIgnoreCase("mtp")){
                         
                if (args.length == 0)
                {
                    PluginDescriptionFile descFile = this.getDescription();
                    sender.sendMessage(ChatColor.GREEN+"-----------------------------------------------------");
                    sender.sendMessage(ChatColor.GREEN+descFile.getFullName() +" by "+descFile.getAuthors());
                    sender.sendMessage(ChatColor.GREEN+ "Type /mtp help for help");
                    sender.sendMessage(ChatColor.GREEN+ "Type /mtp perms for permissions");
                    sender.sendMessage(ChatColor.GREEN+"-----------------------------------------------------");
                    return true;
                }
                
                if (args.length == 2 && args[0].equalsIgnoreCase("remove"))
                {
                    if(sender instanceof Player){
                    if(!permCheck((Player)sender, "minetp.teleportremove")){
                         sender.sendMessage(ChatColor.RED+ this.getConfig().getString("config.errormessages.nopermission"));
                         return true;
                    }
                    if (database.removeTeleport(args[1])){
                        sender.sendMessage(ChatColor.GREEN+ this.getConfig().getString("config.messages.successremove"));
                    }
                    else
                    {
                         sender.sendMessage(ChatColor.RED+ this.getConfig().getString("config.errormessages.removefaild"));
                    }
                    return true;
                    }
                }
                
                if (args.length != 1){
                    sender.sendMessage(ChatColor.RED+this.getConfig().getString("config.errormessages.syntaxerror")+ "/mtp <player>");
                    return true;
                }
                if (args[0].equalsIgnoreCase("perms"))
                {
                    sender.sendMessage(ChatColor.GREEN+ "minetp.teleportoffline: allows to plan a teleport");
                    sender.sendMessage(ChatColor.GREEN+ "minetp.teleportremove: alloes to remove a planed teleport");
                    return true;
                }
                if (args[0].equalsIgnoreCase("help"))
                {
                    sender.sendMessage(ChatColor.GREEN+ "Type /mtp <player> to teleport the player to your position after join.");
                    sender.sendMessage(ChatColor.GREEN+ "Type /mtp remove <player>  to remove a planned teleport");
                    return true;
                }
                
                if(sender instanceof Player){
                if(!permCheck((Player)sender, "minetp.teleportoffline")){
                    sender.sendMessage(ChatColor.RED+ this.getConfig().getString("config.errormessages.nopermission"));
                    return true;
                }  
                
                if (database.firstData(args[0])){
                    database.addData(args[0], sender.getName(), getServer().getPlayer(sender.getName()).getLocation());
                    sender.sendMessage(ChatColor.GREEN+this.getConfig().getString("config.messages.teleportset"));
                }
                else
                {
                    sender.sendMessage(ChatColor.RED+this.getConfig().getString("config.errormessages.usernamealreadyused"));
                }
            }
        }
        
        return true;
    }
    
    
     private void loadConfig(){
       this.getConfig().options().header("MINETP CONFIG");
       this.getConfig().addDefault("config.messages.teleportset", "User will be teleported the next time he is online.");
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
     
    public boolean permCheck(Player player, String permission){
        if(player.isOp() || player.hasPermission(permission)){
            return true;
        }
        return false;
    }
}
