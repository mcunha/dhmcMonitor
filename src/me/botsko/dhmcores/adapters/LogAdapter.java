package me.botsko.dhmcores.adapters;

import java.sql.SQLException;

public interface LogAdapter {
	
	public boolean wasBlockPlaced( int world_id, double d, double f, double g) throws SQLException;

}