/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xtrsource.minetp;

import java.sql.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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
			stat.executeUpdate("create table if not exists teleport_points (username, pitch, world, x, y, yaw, z, teleporter);");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addData(String player, String teleporter, Location l) {
		try {
			PreparedStatement prep = conn.prepareStatement("insert into teleport_points values (?, ?, ?, ?, ?, ?, ?, ?);");

			prep.setString(1, player);
			prep.setFloat(2, l.getPitch());
			prep.setString(3, l.getWorld().getName());
			prep.setDouble(4, l.getX());
			prep.setDouble(5, l.getY());
			prep.setFloat(6, l.getYaw());
			prep.setDouble(7, l.getZ());
			prep.setString(8, teleporter);
			prep.addBatch();
			prep.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
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
