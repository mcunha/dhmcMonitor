package me.botsko.dhmcores;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.botsko.dhmcores.adapters.Hawkeye;
import me.botsko.dhmcores.listeners.OresBlockBreakEvent;
import me.botsko.dhmcores.listeners.OresPlayerInteractEvent;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DhmcOres extends JavaPlugin {

	protected Logger log = Logger.getLogger("Minecraft");
	protected FileConfiguration config;
	public java.sql.Connection conn;
	protected Hawkeye hawkeye;
	
	protected final String LoggingInterface = "hawkeye";
	
	
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
		
		handleConfig();
		
		removeExpiredLocations();
		
		// Temporary way to load db interfaces, just in case we allow for more someday
		if(LoggingInterface == "hawkeye"){
			hawkeye = new Hawkeye(this);
		}
		
		// Assign event listeners
		try {
			getServer().getPluginManager().registerEvents(new OresBlockBreakEvent( this ), this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		getServer().getPluginManager().registerEvents(new OresPlayerInteractEvent( this ), this);
		
	}
	
	
	
	/**
	 * 
	 */
	public void handleConfig(){
		
		config = getConfig();
		
		// database configs
		this.getConfig().set("mysql.hostname", 	this.getConfig().getString("mysql.hostname", "127.0.0.1"));
		this.getConfig().set("mysql.port", 		this.getConfig().getString("mysql.port", "3306"));
		this.getConfig().set("mysql.database", 	this.getConfig().getString("mysql.database", "minecraft"));
		this.getConfig().set("mysql.username", 	this.getConfig().getString("mysql.username", "root"));
		this.getConfig().set("mysql.password", 	this.getConfig().getString("mysql.password", ""));
		
		// other configs
		this.getConfig().set("debug", this.getConfig().get("debug", false) );
		
		saveConfig();
		
	}
	
	
	/**
     * Connects to the MySQL database
     */
	public void dbc(){
    	
        java.util.Properties conProperties = new java.util.Properties();
        conProperties.put("user", this.getConfig().getString("mysql.username") );
        conProperties.put("password", this.getConfig().getString("mysql.password") );
        conProperties.put("autoReconnect", "true");
        conProperties.put("maxReconnects", "3");
        String uri = "jdbc:mysql://"+this.getConfig().getString("mysql.hostname")+":"+this.getConfig().getString("mysql.port")+"/"+this.getConfig().getString("mysql.database");
        
        try {
        	conn = DriverManager.getConnection(uri, conProperties);
        } catch (SQLException e) {
            log.throwing("me.botsko.dhmcstats", "dbc()", e);
        }
    }
	
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public Hawkeye getLoggingInterface(){
		if(LoggingInterface == "hawkeye"){
			return hawkeye;
		}
		return null;
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
		    		if(diff >= 300){
		    			alertedBlocks.remove(entry.getKey());
		    		}
		    	}
		    	log("AlertedBlock Size: " + alertedBlocks.size() );
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
