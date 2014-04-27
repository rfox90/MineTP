/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xtrsource.minetp;

import java.sql.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * 
 * @author Thomas
 */
public class DatabaseUtil {

	Connection	conn	= null;
	Statement	stat;
	ResultSet	rs;

	MineTP		plugin;

	public DatabaseUtil(MineTP plugin) {
		this.plugin = plugin;

		loadDatabase();
	}

	public void loadDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/minetp.db");
			stat = conn.createStatement();
			stat.executeUpdate("create table if not exists teleport_points (id,username, pitch, world, x, y, yaw, z, teleporter,teleporter_id);");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean addData(Player target, Player sender) {
		try {
			PreparedStatement prep = conn.prepareStatement("insert into teleport_points values (?,?, ?, ?, ?, ?, ?, ?, ?,?);");
			prep.setString(1, target.getUniqueId().toString());
			prep.setString(2, target.getName());
			Location l = sender.getLocation();
			prep.setFloat(3, l.getPitch());
			prep.setString(4, l.getWorld().getName());
			prep.setDouble(5, l.getX());
			prep.setDouble(6, l.getY());
			prep.setFloat(7, l.getYaw());
			prep.setDouble(8, l.getZ());
			prep.setString(9, sender.getName());
			prep.setString(10, sender.getUniqueId().toString());
			prep.addBatch();
			int[] res = prep.executeBatch();
			if(res[0]>=0 || res[0]== PreparedStatement.SUCCESS_NO_INFO) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Location getLocation(String player) {
		try {
			rs = stat.executeQuery("select * from teleport_points;");
			while (rs.next()) {
				if (rs.getString("username").equals(player)) {
					Location l = new Location(Bukkit.getWorld(rs.getString("world")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch"));
					stat.execute("DELETE FROM teleport_points WHERE username = '" + player + "';");
					rs.close();
					return l;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Boolean firstData(String player) {
		try {
			rs = stat.executeQuery("select * from teleport_points;");
			while (rs.next()) {
				if (rs.getString("username").equals(player)) {
					rs.close();
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public String getTeleporterName(String player) {
		try {
			rs = stat.executeQuery("select * from teleport_points;");
			while (rs.next()) {
				if (rs.getString("username").equals(player)) {
					String name = rs.getString("teleporter");
					rs.close();
					return name;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "ERROR";
	}

	public Boolean removeTeleport(String player) {
		try {
			stat.execute("DELETE FROM teleport_points WHERE username = '" + player + "';");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
