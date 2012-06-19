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
	
	String[] illegalCommands = {"op","stop","reload","deop"};
	
	
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
        	String msg = player.getName() + " attempted an illegal command: " + cmd;
            if (cmd.startsWith("/"+s)){
            	player.sendMessage( plugin.playerError("You have attempted an illegal command. Staff has been notified immediately and the event is logged.") );
            	plugin.alertPlayers( msg );
            	event.setCancelled(true);
            	plugin.log(msg);
            }
        }
    }

}
