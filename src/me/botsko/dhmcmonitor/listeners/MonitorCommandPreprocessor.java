package me.botsko.dhmcmonitor.listeners;

import java.util.List;

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
	
	List<String> illegalCommands;
	
	
	/**
	 * 
	 * @param plugin
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public MonitorCommandPreprocessor( DhmcMonitor plugin ){
		this.plugin = plugin;
		illegalCommands = (List<String>) plugin.getConfig().getList("illegal_commands");
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
            if (cmd.equalsIgnoreCase("/"+s) || cmd.startsWith("/"+s+" ")){
            	player.sendMessage( plugin.playerError("You have attempted an illegal command. Staff has been notified immediately and the event is logged.") );
            	plugin.alertPlayers( msg );
            	event.setCancelled(true);
            	plugin.log(msg);
            }
        }
    }

}
