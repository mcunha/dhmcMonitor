package me.botsko.dhmcmonitor.listeners;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.botsko.dhmcmonitor.DhmcMonitor;
import me.botsko.dhmcmonitor.adapters.LogAdapter;


public class MonitorBlockBreakEvent implements Listener {
	
	/**
	 * 
	 */
	private DhmcMonitor plugin;
	
	/**
	 * 
	 */
	protected HashMap<String,Integer> worlds;
	
	
	/**
	 * 
	 * @param plugin
	 * @throws SQLException 
	 */
	public MonitorBlockBreakEvent( DhmcMonitor plugin ) throws SQLException{
		this.plugin = plugin;
		if(plugin.getConfig().getBoolean("hawkeye.enabled")){
			worlds = plugin.getLoggingInterface().getWorldIds();
		}
	}

	
	/**
	 * Award the player when blocks break if the chosen spell supports it.
	 * @param event
	 * @throws SQLException 
	 */
	@EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(final BlockBreakEvent event) throws SQLException{
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		if(player.getGameMode() != GameMode.CREATIVE){
			
			if(block != null && isWatched(block) && !plugin.alertedBlocks.containsKey( block.getLocation() )){
			
				// check if block placed
				boolean wasplaced = false;
				if(plugin.getConfig().getBoolean("hawkeye.enabled")){
					LogAdapter la = plugin.getLoggingInterface();
					wasplaced = la.wasBlockPlaced( worlds.get( block.getWorld().getName() ), block.getLocation().getX(),block.getLocation().getY(),block.getLocation().getZ() );
				}
				
				if(!wasplaced){
					// identify all ore blocks on same Y axis in x/z direction
					ArrayList<Block> matchingBlocks = new ArrayList<Block>();
					ArrayList<Block> foundores = findNeighborBlocks( block.getType(), block, matchingBlocks );
					if(!foundores.isEmpty()){
						
						// Save the block
						BlockState state = block.getState();
						
						// Set to air to get the light
						block.setType(Material.AIR);
						int light = block.getLightLevel();
						light = (light > 0 ? Math.round(((light) & 0xFF) * 100) / 15 : 0);
						
						// Restore the block
						block.setType( state.getType() );
						
						String msg = getOreColor(block) + player.getName() + " found " + foundores.size() + " " + getOreNiceName(block) + " " + light + "% light";
						plugin.alertPlayers(msg);
						plugin.log( msg );
					}
				}
			}
		}
	}
	
	
	/**
	 * 
	 * @param block
	 * @return
	 */
	protected ChatColor getOreColor( Block block ){
		Material type = block.getType();
		switch(type){
			case DIAMOND_ORE:
				return ChatColor.AQUA;
			case LAPIS_ORE:
				return ChatColor.BLUE;
			case GOLD_ORE:
				return ChatColor.GOLD;
			case IRON_ORE:
				return ChatColor.GRAY;
			case GLOWING_REDSTONE_ORE:
			case REDSTONE_ORE:
				return ChatColor.RED;
//			case EMERALD_ORE:
//				return ChatColor.GREEN;
			default:
				return ChatColor.WHITE;
		}
	}
	
	
	/**
	 * 
	 * @param block
	 * @return
	 */
	protected String getOreNiceName( Block block ){
		return block.getType().toString().replace("_", " ").toLowerCase().replace("glowing", " ");
	}
	
	
	/**
	 * 
	 * @param block
	 * @return
	 */
	protected boolean isWatched( Block block ){
		
		Material type = block.getType();
		FileConfiguration config = plugin.getConfig();
		
		if(type == Material.DIAMOND_ORE && config.getBoolean("alerts.ores.diamond")){
			return true;
		}
		if(type == Material.GOLD_ORE && config.getBoolean("alerts.ores.gold")){
			return true;
		}
		if(type == Material.IRON_ORE && config.getBoolean("alerts.ores.iron")){
			return true;
		}
		if(type == Material.LAPIS_ORE && config.getBoolean("alerts.ores.lapis")){
			return true;
		}
		if( (type == Material.GLOWING_REDSTONE_ORE || type == Material.REDSTONE_ORE) && config.getBoolean("alerts.ores.redstone")){
			return true;
		}
		if(type == Material.COAL_ORE && config.getBoolean("alerts.ores.coal")){
			return true;
		}
//		if(type == Material.EMERALD_ORE && config.getBoolean("alerts.ores.emerald")){
//			return true;
//		}
		return false;
	}
	
	
	/**
	 *    
	 * @param currBlock
	 * @param toBeFelled
	 */
    private ArrayList<Block> findNeighborBlocks( Material type, Block currBlock, ArrayList<Block> matchingBlocks ) {

        if(isWatched(currBlock)){
        	
        	matchingBlocks.add(currBlock);
        	java.util.Date date = new java.util.Date();
        	plugin.alertedBlocks.put(currBlock.getLocation(), date.getTime());
        	
        	for(int x = -1; x <= 1; x++){
        		for(int z = -1; z <= 1; z++){
        			for(int y = -1; y <= 1; y++){
	        			Block newblock = currBlock.getRelative(x, y, z);
	        			// ensure it matches the type and wasn't already found
	        			if( newblock.getType() == type && !matchingBlocks.contains(newblock) ){
	        				findNeighborBlocks( type, newblock, matchingBlocks );
	        			}
	        		}
        		}
        	}
        }
        
        return matchingBlocks;
        
    }
}
