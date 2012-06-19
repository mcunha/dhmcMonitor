package me.botsko.dhmcmonitor;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import me.botsko.dhmcmonitor.adapters.Hawkeye;
import me.botsko.dhmcmonitor.adapters.LogAdapter;
import me.botsko.dhmcmonitor.listeners.MonitorBlockBreakEvent;
import me.botsko.dhmcmonitor.listeners.MonitorBlockPlaceEvent;
import me.botsko.dhmcmonitor.listeners.MonitorCommandPreprocessor;
import me.botsko.dhmcmonitor.listeners.MonitorPlayerBucketEmptyEvent;
import me.botsko.dhmcmonitor.listeners.MonitorPlayerChatEvent;
import me.botsko.dhmcmonitor.listeners.MonitorPlayerInteractEvent;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DhmcMonitor extends JavaPlugin {

	protected Logger log = Logger.getLogger("Minecraft");
	protected FileConfiguration config;
	public java.sql.Connection conn;
	protected LogAdapter loggingInterface = null;
	protected final String log_prefix = "[!]: ";
	
	
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
		
		this.log("Initializing plugin. By Viveleroi, Darkhelmet Minecraft: s.dhmc.us");
		
		// Load configuration, or install if new
		MonitorConfig mc = new MonitorConfig( this );
		config = mc.getConfig();
		
		removeExpiredLocations();
		
		// Assign event listeners
		try {
			getServer().getPluginManager().registerEvents(new MonitorBlockBreakEvent( this ), this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		getServer().getPluginManager().registerEvents(new MonitorPlayerInteractEvent( this ), this);
		getServer().getPluginManager().registerEvents(new MonitorPlayerChatEvent( this ), this);
		getServer().getPluginManager().registerEvents(new MonitorBlockPlaceEvent( this ), this);
		getServer().getPluginManager().registerEvents(new MonitorPlayerBucketEmptyEvent( this ), this);
		getServer().getPluginManager().registerEvents(new MonitorCommandPreprocessor( this ), this);
		
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
	public LogAdapter getLoggingInterface(){
		if(getConfig().getBoolean("hawkeye.enabled")){
			loggingInterface = new Hawkeye(this);
		}
		return loggingInterface;
	}
	
	
	/**
	 * 
	 */
	public void removeExpiredLocations(){
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {

		    public void run() {
		    	java.util.Date date = new java.util.Date();
		    	// Remove locations logged over five minute ago.
		    	for (Entry<Location, Long> entry : alertedBlocks.entrySet()){
		    		long diff = (date.getTime() - entry.getValue()) / 1000;
		    		System.out.print("AlertedBlock diff: " + diff);
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
	 */
	public void alertPlayers( String msg ){
		for (Player p : getServer().getOnlinePlayers()) {
			if (p.hasPermission("dhmcmonitor.alert")){
				p.sendMessage( playerMsg( msg ) );
			}
		}
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public String playerMsg(String msg){
		if(msg != null){
			return ChatColor.RED + log_prefix + ChatColor.WHITE + msg;
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
			return ChatColor.RED + log_prefix + ChatColor.RED + msg;
		}
		return "";
	}

	
	
	/**
	 * 
	 * @param message
	 */
	public void log(String message){
		log.info(log_prefix + message);
	}
	
	
	/**
	 * 
	 * @param message
	 */
	public void debug(String message){
		if(this.getConfig().getBoolean("debug")){
			log.info(log_prefix + message);
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
