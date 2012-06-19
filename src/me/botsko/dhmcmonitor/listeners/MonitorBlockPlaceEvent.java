package me.botsko.dhmcmonitor.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.botsko.dhmcmonitor.DhmcMonitor;


public class MonitorBlockPlaceEvent implements Listener {
	
	/**
	 * 
	 */
	private DhmcMonitor plugin;
	
	
	/**
	 * 
	 * @param plugin
	 * @throws SQLException 
	 */
	public MonitorBlockPlaceEvent( DhmcMonitor plugin ){
		this.plugin = plugin;
	}

	
	/**
	 * Award the player when blocks break if the chosen spell supports it.
	 * @param event
	 * @throws SQLException 
	 */
	@EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(final BlockPlaceEvent event){
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		if(block.getType() == Material.TNT){
			plugin.alertPlayers( player.getName() + " placed tnt." );
		}
	}
}
