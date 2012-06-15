package me.botsko.dhmcores.listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.botsko.dhmcores.DhmcOres;


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
	 * @param plugin
	 */
	public OresBlockBreakEvent( DhmcOres plugin ){
		this.plugin = plugin;
	}

	
	/**
	 * Award the player when blocks break if the chosen spell supports it.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(final BlockBreakEvent event){
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
//		System.out.print("Block Location: " + block.getLocation().toString());
//		System.out.print("Alerted Has Location: " + alertedBlocks.contains( block.getLocation() ));
		if(player.getGameMode() != GameMode.CREATIVE){
			if(block != null && isWatched(block) && !plugin.alertedBlocks.containsKey( block.getLocation() )){
				// identify all ore blocks on same Y axis in x/z direction
				ArrayList<Block> matchingBlocks = new ArrayList<Block>();
				ArrayList<Block> foundores = findNeighborBlocks( block.getType(), block, matchingBlocks );
				if(!foundores.isEmpty()){
					for (Player p : player.getServer().getOnlinePlayers()) {
						if (p.hasPermission("dhmcores.alert")){
//							int light = Math.round(((block.getLightLevel()) & 0xFF) * 100) / 15;
							p.sendMessage( plugin.playerMsg( getOreColor(block) + player.getName() + " found " + foundores.size() + " " + getOreNiceName(block) ) );
						}
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
        	
//        	System.out.print("Found Block Location: " + currBlock.getLocation().toString());
        	
        	Block xPositive = currBlock.getRelative(1, 0, 0);
            Block xNegative = currBlock.getRelative(-1, 0, 0);
            Block zPositive = currBlock.getRelative(0, 0, 1);
            Block zNegative = currBlock.getRelative(0, 0, -1);
            Block yPositive = currBlock.getRelative(0, 1, 0);
            Block yNegative = currBlock.getRelative(0, -1, 0);

            // n
        	if ( !matchingBlocks.contains(xPositive) ) {
        		findNeighborBlocks( type, xPositive, matchingBlocks );
        	}
        	// s
        	if ( !matchingBlocks.contains(xNegative) ) {
        		findNeighborBlocks( type, xNegative, matchingBlocks );
        	}
        	// e
        	if ( !matchingBlocks.contains(zPositive) ) {
        		findNeighborBlocks( type, zPositive, matchingBlocks );
        	}
        	// w
        	if ( !matchingBlocks.contains(zNegative) ) {
        		findNeighborBlocks( type, zNegative, matchingBlocks );
        	}
        	// u
        	if ( !matchingBlocks.contains(yPositive) ) {
        		findNeighborBlocks( type, yPositive, matchingBlocks );
        	}
        	// d
        	if ( !matchingBlocks.contains(yNegative) ) {
        		findNeighborBlocks( type, yNegative, matchingBlocks );
        	}
        }
        
        return matchingBlocks;
        
    }
}
