package me.botsko.dhmcmonitor.commands;


import me.botsko.dhmcmonitor.DhmcMonitor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MonitorCommandExecutor implements CommandExecutor {
	
	
	private final DhmcMonitor plugin;

	
	/**
	 * 
	 * @param plugin
	 */
	public MonitorCommandExecutor(DhmcMonitor plugin) {
		this.plugin = plugin;
	}

	
	/**
	 * 
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (label.equalsIgnoreCase("monitor")){
				
				if(args.length > 0 && args[0].equalsIgnoreCase("reload")){
					if(player.hasPermission("dhmcmonitor.reload") || player.hasPermission("dhmcmonitor.*")){
						plugin.loadConfigs();
						player.sendMessage( plugin.playerMsg("Configuration reloaded.") );
						return true;
					}
				}
			}
		} else {
			if(args.length > 0 && args[0].equalsIgnoreCase("reload")){
				plugin.loadConfigs();
				sender.sendMessage( plugin.playerMsg("Configuration reloaded.") );
				return true;
			}
		}
		return false;		
	}
}
