package me.botsko.dhmcores;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.botsko.dhmcores.listeners.OresBlockBreakEvent;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DhmcOres extends JavaPlugin {

	protected Logger log = Logger.getLogger("Minecraft");
	protected FileConfiguration config;
	
	
	/**
	 * Store locations of blocks already alerted so we don't count
	 * them again. This is only temporary.
	 */
	public ConcurrentHashMap<Location,Long> alertedBlocks = new ConcurrentHashMap<Location,Long>();
	
	
    /**
     * Enables the plugin and activates our player listeners
     */
	@Override
	public void onEnable(){
		
		this.log("Initializing plugin.");
		
		removeExpiredLocations();
		
		// Assign event listeners
		getServer().getPluginManager().registerEvents(new OresBlockBreakEvent( this ), this);
		
	}
	
	
	/**
	 * If a user disconnects in an unknown way that is never caught by onPlayerQuit,
	 * this will force close all records except for players currently online.
	 */
	public void removeExpiredLocations(){
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

		    public void run() {
		    	java.util.Date date = new java.util.Date();
		    	// Remove locations logged over five minute ago.
		    	for (Entry<Location, Long> entry : alertedBlocks.entrySet()){
		    		long diff = (date.getTime() - entry.getValue()) / 1000;
		    		if(diff >= 30){
		    			alertedBlocks.remove(entry.getKey());
		    		}
		    	}
		    }
		}, 1200L, 1200L);	
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String playerMsg(String msg){
		if(msg != null){
			return ChatColor.RED + "[ORE]: " + ChatColor.WHITE + msg;
		}
		return "";
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String playerError(String msg){
		if(msg != null){
			return ChatColor.RED + "[ORE]: " + ChatColor.RED + msg;
		}
		return "";
	}

	
	
	/**
	 * 
	 * @param message
	 */
	public void log(String message){
		log.info("[ORE]: " + message);
	}
	
	
	/**
	 * 
	 * @param message
	 */
	public void debug(String message){
		if(this.getConfig().getBoolean("debug")){
			log.info("[ORE]: " + message);
		}
	}
	
	
	/**
	 * Shutdown
	 */
	@Override
	public void onDisable(){
		this.log("Closing plugin.");
	}	
}
