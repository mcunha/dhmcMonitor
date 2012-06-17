package me.botsko.dhmcores.listeners;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.botsko.dhmcores.DhmcOres;
import me.botsko.dhmcores.adapters.Hawkeye;


public class OresBlockBreakEvent implements Listener {
	
	/**
	 * 
	 */
	private DhmcOres plugin;

	
	/**
	 * It's the date yo!
	 */
	protected java.util.Date date = new java.util.Date();
	
	/**
	 * 
	 */
	protected HashMap<String,Integer> worlds;
	
	
	/**
	 * 
	 * @param plugin
	 * @throws SQLException 
	 */
	public OresBlockBreakEvent( DhmcOres plugin ) throws SQLException{
		this.plugin = plugin;
		worlds = plugin.getLoggingInterface().getWorldIds();
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
		
//		System.out.print("Block Location: " + block.getLocation().toString());
//		System.out.print("Alerted Has Location: " + alertedBlocks.contains( block.getLocation() ));
		if(player.getGameMode() != GameMode.CREATIVE){
			
			if(block != null && isWatched(block) && !plugin.alertedBlocks.containsKey( block.getLocation() )){
			
				// check if block placed
				Hawkeye hk = plugin.getLoggingInterface();
				boolean wasplaced = hk.wasBlockPlaced( worlds.get( block.getWorld().getName() ), block.getLocation().getX(),block.getLocation().getY(),block.getLocation().getZ() );
			
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
						for (Player p : player.getServer().getOnlinePlayers()) {
							if (p.hasPermission("dhmcores.alert")){
								p.sendMessage( plugin.playerMsg( msg ) );
							}
						}
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
		return block.getType().toString().replace("_", " ").toLowerCase();
	}
	
	
	/**
	 * 
	 * @param block
	 * @return
	 */
	protected boolean isWatched( Block block ){
		Material type = block.getType();
		return (type == Material.DIAMOND_ORE || type == Material.GOLD_ORE || type == Material.LAPIS_ORE || type == Material.IRON_ORE);
	}
	
	
	
	/**
	 *    
	 * @param currBlock
	 * @param toBeFelled
	 */
    private ArrayList<Block> findNeighborBlocks( Material type, Block currBlock, ArrayList<Block> matchingBlocks ) {

        if(isWatched(currBlock)){
        	
        	matchingBlocks.add(currBlock);
        	plugin.alertedBlocks.put(currBlock.getLocation(), date.getTime());
        	
        	for(int x = -1; x <= 1; x++){
        		for(int z = -1; z <= 1; z++){
        			for(int y = -1; y <= 1; y++){
	        			Block newblock = currBlock.getRelative(x, y, z);
//	        			System.out.print("COORDS " + x + " " + z + " " + newblock.getTypeId());
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
