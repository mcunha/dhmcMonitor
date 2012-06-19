package me.botsko.dhmcmonitor.listeners;

import me.botsko.dhmcmonitor.DhmcMonitor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MonitorCommandPreprocessor implements Listener {
	
	
	/**
	 * 
	 */
	private DhmcMonitor plugin;
	
	String[] illegalCommands = {"op"};
	
	
	/**
	 * 
	 * @param plugin
	 * @throws SQLException 
	 */
	public MonitorCommandPreprocessor( DhmcMonitor plugin ){
		this.plugin = plugin;
	}
	
	
	/**
     * Log all commands
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String cmd = event.getMessage();
        
        for (String s : illegalCommands){
            if (cmd.startsWith(s)){
            	plugin.alertPlayers( player.getName() + " attempted an illegal command: " + cmd );
            	event.setCancelled(true);
            }
        }
    }

}
