package me.botsko.dhmcmonitor.listeners;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import me.botsko.dhmcmonitor.DhmcMonitor;


public class MonitorPlayerBucketEmptyEvent implements Listener {
	
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
	public MonitorPlayerBucketEmptyEvent( DhmcMonitor plugin ){
		this.plugin = plugin;
	}

	
	/**
	 * Award the player when blocks break if the chosen spell supports it.
	 * @param event
	 * @throws SQLException 
	 */
	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event){
		Player player = event.getPlayer();
		if(player.getItemInHand().getType() == Material.LAVA_BUCKET){
			
			// Existing count
			int count = 0;
			
			if(countedEvents.containsKey(player)){
				count = countedEvents.get(player);
			}
			count = count + 1;
			countedEvents.put(player, count );
			
			if(count == 5){
				String msg = ChatColor.GRAY + player.getName() + " continues to place lava - pausing warnings.";
				plugin.alertPlayers(msg);
			} else {
				if(count < 5){
					String msg = ChatColor.GRAY + player.getName() + " placed lava with a bucket";
					plugin.alertPlayers(msg);
				}
			}
		}
	}
}
