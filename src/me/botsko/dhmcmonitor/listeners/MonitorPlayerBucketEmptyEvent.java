package me.botsko.dhmcmonitor.listeners;

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
			plugin.alertPlayers( player.getName() + " placed lava with a bucket." );
		}
	}
}
