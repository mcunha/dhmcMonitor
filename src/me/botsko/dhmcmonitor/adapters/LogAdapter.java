package me.botsko.dhmcmonitor.adapters;

import java.sql.SQLException;
import java.util.HashMap;

public interface LogAdapter {
	
	public boolean wasBlockPlaced( int world_id, double d, double f, double g) throws SQLException;

	public HashMap<String, Integer> getWorldIds() throws SQLException;

}