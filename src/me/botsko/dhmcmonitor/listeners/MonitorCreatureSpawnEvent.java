package me.botsko.dhmcmonitor.listeners;

import java.sql.SQLException;

import me.botsko.dhmcmonitor.DhmcMonitor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class MonitorCreatureSpawnEvent implements Listener {
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private DhmcMonitor plugin;

	/**
	 * 
	 * @param plugin
	 * @throws SQLException 
	 */
	public MonitorCreatureSpawnEvent( DhmcMonitor plugin ){
		this.plugin = plugin;
	}

	/**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled()) return;
		if (event.getSpawnReason() != SpawnReason.BUILD_WITHER) return;
		
		event.setCancelled(true);
	}
}
