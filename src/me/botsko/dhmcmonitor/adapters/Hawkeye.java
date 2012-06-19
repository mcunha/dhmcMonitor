package me.botsko.dhmcmonitor.adapters;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import me.botsko.dhmcmonitor.DhmcMonitor;

public class Hawkeye implements LogAdapter {
	
	protected DhmcMonitor plugin;
	
	
	/**
	 * 
	 * @param plugin
	 */
	public Hawkeye( DhmcMonitor plugin ){
		this.plugin = plugin;
	}
	
	
	/**
	 * 
	 * @param username
	 * @param item
	 * @param type
	 * @param amount
	 * @param total
	 * @throws SQLException 
	 */
	public boolean wasBlockPlaced( int world_id, double d, double f, double g) throws SQLException{
		
		boolean found = false;
		
		if (plugin.conn == null || plugin.conn.isClosed()) plugin.dbc();
		
		String hawk_table = plugin.getConfig().getString("hawkeye.tablename");
		
		if( !hawk_table.isEmpty() ){
		
			String s = String.format("SELECT COUNT(*) as rows FROM "+hawk_table+" WHERE x = ? AND y = ? AND z = ? AND action = 1 AND world_id = ?");
	        PreparedStatement pstmt = plugin.conn.prepareStatement(s);
	        pstmt.setDouble(1, d);
	        pstmt.setDouble(2, f);
	        pstmt.setDouble(3, g);
	        pstmt.setInt(4, world_id);
	        pstmt.execute();
			ResultSet ids = pstmt.getResultSet();
			
			try {
				ids.first();
				if(ids.getInt("rows") > 0){
					found = true;
				}
				ids.close();
				pstmt.close();
				plugin.conn.close();
			} catch(Exception e){
			}
		}
        return found;
	}
	
	
	/**
	 * 
	 * @param world
	 * @return
	 * @throws SQLException 
	 */
	public HashMap<String,Integer> getWorldIds() throws SQLException{
		
		HashMap<String,Integer> worlds = new HashMap<String,Integer>();
		
		if (plugin.conn == null || plugin.conn.isClosed()) plugin.dbc();
		
		String hawk_table = plugin.getConfig().getString("hawkeye.tablename_worlds");
		if( !hawk_table.isEmpty() ){
			
			String s = String.format("SELECT * FROM "+hawk_table);
	        PreparedStatement pstmt = plugin.conn.prepareStatement(s);
	        pstmt.execute();
			ResultSet rs = pstmt.getResultSet();
			
			try {
	    		while(rs.next()){
	    			worlds.put(rs.getString("world"),Integer.parseInt(rs.getString("world_id")));
	    		}
				rs.close();
				pstmt.close();
				plugin.conn.close();
			} catch(Exception e){
			}
		}
		return worlds;
	}
}