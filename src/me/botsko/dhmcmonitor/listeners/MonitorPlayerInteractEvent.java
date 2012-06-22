package me.botsko.dhmcmonitor.listeners;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import me.botsko.dhmcmonitor.DhmcMonitor;


public class MonitorPlayerInteractEvent implements Listener {
	
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
	public MonitorPlayerInteractEvent( DhmcMonitor plugin ){
		this.plugin = plugin;
	}

	
	/**
	 * Award the player when blocks break if the chosen spell supports it.
	 * @param event
	 * @throws SQLException 
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteractEvent(final PlayerInteractEvent event){
		
		// Ensure they're right-clicking
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
		
			Player player = event.getPlayer();
			
			// are they using flint?
			if(player.getItemInHand().getType() == Material.FLINT_AND_STEEL){
				
				// Existing count
				int count = 0;
				
				if(countedEvents.containsKey(player)){
					count = countedEvents.get(player);
				}
				count = count + 1;
				countedEvents.put(player, count );
				
				if(count == 5){
					String msg = ChatColor.GRAY + player.getName() + " continues to use flint and steel - pausing warnings.";
					plugin.alertPlayers(msg);
				} else {
					if(count < 5){
						String msg = ChatColor.GRAY + player.getName() + " used flint and steel";
						plugin.alertPlayers(msg);
					}
				}
			}
		}
	}
	
	
	/**
	 * 
	 */
	public void removeExpiredLocations(){
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
		    public void run() {
		    	countedEvents = new ConcurrentHashMap<Player,Integer>();
		    }
		}, 18000L, 18000L);
	}
}
