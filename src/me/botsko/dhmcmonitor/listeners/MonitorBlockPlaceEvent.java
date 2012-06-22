package me.botsko.dhmcmonitor.listeners;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
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
	 */
	public ConcurrentHashMap<Player,Integer> countedEvents = new ConcurrentHashMap<Player,Integer>();
	
	
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
			
			// Existing count
			int count = 0;
			
			if(countedEvents.containsKey(player)){
				count = countedEvents.get(player);
			}
			count = count + 1;
			countedEvents.put(player, count );
			
			if(count == 5){
				String msg = ChatColor.GRAY + player.getName() + " continues to place tnt - pausing warnings.";
				plugin.alertPlayers(msg);
			} else {
				if(count < 5){
					String msg = ChatColor.GRAY + player.getName() + " placed tnt";
					plugin.alertPlayers(msg);
				}
			}
		}
	}
}
