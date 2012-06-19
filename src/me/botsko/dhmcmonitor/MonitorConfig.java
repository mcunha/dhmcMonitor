package me.botsko.dhmcmonitor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MonitorConfig {
	
	protected DhmcMonitor plugin;
	
	
	/**
	 * 
	 * @param plugin
	 */
	public MonitorConfig( DhmcMonitor plugin ) {
		this.plugin = plugin;
	}
	
	
	/**
	 * 
	 * @param plugin
	 */
	public FileConfiguration getConfig(){
		
		FileConfiguration config = plugin.getConfig();
		
		// set defaults
		config.addDefault("debug", false);
		config.addDefault("mysql.hostname", "127.0.0.1");
		config.addDefault("mysql.port", 3306);
		config.addDefault("mysql.database", "minecraft");
		config.addDefault("mysql.username", "root");
		config.addDefault("mysql.password", "");
		
		List<String> illegal_commands = new ArrayList<String>();
		illegal_commands.add("op");
		illegal_commands.add("deop");
		illegal_commands.add("stop");
		illegal_commands.add("reload");
		config.addDefault("illegal_commands", illegal_commands);
		
		config.addDefault("alerts.ores.coal", false);
		config.addDefault("alerts.ores.redstone", false);
		config.addDefault("alerts.ores.lapis", true);
		config.addDefault("alerts.ores.iron", true);
		config.addDefault("alerts.ores.gold", true);
		config.addDefault("alerts.ores.diamond", true);
		
		config.addDefault("censors.limit_text_caps", true);
		config.addDefault("censors.block_fake_censor", true);
		config.addDefault("censors.censor_profanity", true);
		
		config.addDefault("hawkeye.enabled", true);
		config.addDefault("hawkeye.tablename", "hawkeye");
		config.addDefault("hawkeye.tablename_worlds", "hawk_worlds");
		
		// Copy defaults
		config.options().copyDefaults(true);
		
		// save the defaults/config
		plugin.saveConfig();
		
		return config;
		
	}
	
	
	/**
	 * Loads language configuration
	 * @return
	 */
	public FileConfiguration getProfanityConfig(){
		
		// Read the base config
		FileConfiguration config = loadConfig( "defaults/", "profanity" );
		
		// copy defaults and save config
		config.options().copyDefaults(true);
		write( "profanity", config );
		
		return config;
		
	}
	
	
	/**
	 * Returns base directory for config
	 * @return
	 */
	protected File getDirectory(){
		File dir = new File(plugin.getDataFolder()+"");
		return dir;
	}
	
	
	/**
	 * Returns chosen filename with directory
	 * @return
	 */
	protected File getFilename( String filename ){
		File file = new File(getDirectory(), filename + ".yml");
		return file;
	}
	
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	protected FileConfiguration loadConfig( String default_folder, String filename ){
		File file = getFilename( filename );
		if(file.exists()){
			return YamlConfiguration.loadConfiguration(file);
		} else {
			// Look for defaults in the jar
		    InputStream defConfigStream = plugin.getResource(default_folder+filename+".yml");
		    if (defConfigStream != null) {
		        return YamlConfiguration.loadConfiguration(defConfigStream);
		    }
		    return null;
		}
	}
	
	
	/**
	 * 
	 * @param config
	 */
	protected void saveConfig( String filename, FileConfiguration config ){
		File file = getFilename( filename );
		try {
			config.save(file);
		} catch (IOException e) {
			plugin.log("Could not save the configuration file to "+file);
		}
	}
	
	
	/**
	 * 
	 */
	protected void write( String filename, FileConfiguration config ){
		try {
			BufferedWriter bw = new BufferedWriter( new FileWriter( getFilename( filename ), true ) );
			saveConfig( filename, config );
			bw.flush();
			bw.close();
		} catch (IOException e){

        }
	}
}
