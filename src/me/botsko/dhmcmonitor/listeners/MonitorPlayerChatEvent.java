package me.botsko.dhmcmonitor.listeners;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

import me.botsko.dhmcmonitor.DhmcMonitor;
import me.botsko.dhmcmonitor.MonitorConfig;


/**
 * 
 * @author botskonet
 *
 */
public class MonitorPlayerChatEvent implements Listener {
	
	/**
	 * 
	 */
	private DhmcMonitor plugin;
	
	/**
	 * 
	 */
	protected FileConfiguration rejectedProfanity;
	
	/**
	 * 
	 */
	protected List<String> rejectWords;
	
	/**
	 * 
	 */
	protected HashMap<String,String> leet = new HashMap<String,String>();
	
	
	/**
	 * 
	 * @param plugin
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public MonitorPlayerChatEvent( DhmcMonitor plugin ){
		this.plugin = plugin;
		MonitorConfig mc = new MonitorConfig( plugin );
		rejectedProfanity = mc.getProfanityConfig();
		rejectWords = (List<String>) rejectedProfanity.getList("reject-words");
		
		// Build leet conversion.
		leet.put("1", "l");
		leet.put("1", "i");
		leet.put("2", "z");
		leet.put("2", "r");
		leet.put("3", "e");
		leet.put("4", "h");
		leet.put("4", "a");
		leet.put("5", "s");
		leet.put("6", "g");
		leet.put("7", "l");
		leet.put("8", "a");
		leet.put("9", "p");
		leet.put("9", "g");
		leet.put("0", "o");
		leet.put("13", "b");
		leet.put("44", "m");
	}

	
	/**
	 * Award the player when blocks break if the chosen spell supports it.
	 * @param event
	 * @throws SQLException 
	 */
	@EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(final PlayerChatEvent event){
		
		String msg = event.getMessage();
		Player player = event.getPlayer();
		
		// sorry man, no caps
		if(capsPercentage(msg) > 20){
			msg = msg.toLowerCase();
			event.setMessage( msg );
		}
	
		if(containsSuspectedProfanity(msg)){
			
			event.setCancelled(true);
			player.sendMessage( plugin.playerError("Profanity, or trying to bypass the censor is NOT allowed.") );
			
			String alert_msg = player.getName() + " was warned for profanity.";
			plugin.alertPlayers(alert_msg);
			plugin.log( alert_msg );
		}
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	public double capsPercentage(String msg){
		int count = 0;
		for (char ch : msg.toCharArray()){
			if (Character.isUpperCase(ch)){
				count++;
			}
		}
		return (count > 0 ? (( (double)count / (double)msg.length()) * 100) : 100);
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	protected boolean containsSuspectedProfanity( String msg ){
		
		// ensure lower case
		String _tmp = msg.toLowerCase();
		
		// replace all invalid characters
		_tmp = _tmp.replaceAll("[^a-z0-9]", "");
		
		// get possible leet versions
		List<String> variations = convertLeetSpeak(_tmp);
		
		for(String variation : variations){
			// scan for illegal words
			for(String w : rejectWords){
				if(variation.contains(w.toString())){
					return true;
				}
			}
		}
	
		return false;
	}
	
	
	/**
	 * 
	 * @param msg
	 * @return
	 */
	protected List<String> convertLeetSpeak( String msg ){
		
		// Begin list of all variations, including original
		List<String> _variations = new ArrayList<String>();
		_variations.add(msg);
		
		for (Entry<String, String> entry : leet.entrySet()){
			if(msg.contains( entry.getKey() )){
				// entry.getValue()
				_variations.add( msg.replace(entry.getKey(), entry.getValue()) );
			}
		}
		
		return _variations;
		
	}
}
