package me.botsko.dhmcores.listeners;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.botsko.dhmcores.DhmcOres;


public class OresBlockBreakEvent implements Listener {
	
	private DhmcOres plugin;
	
	/**
	 * Store locations of blocks already alerted so we don't count
	 * them again. This is only temporary.
	 */
	protected ArrayList<Location> alertedBlocks = new ArrayList<Location>();
	
	
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
		
		if(block != null && isWatched(block) && !alertedBlocks.contains( block.getLocation() )){
			// identify all ore blocks on same Y axis in x/z direction
			ArrayList<Block> matchingBlocks = new ArrayList<Block>();
			ArrayList<Block> foundores = findNeighborBlocks( block.getType(), block, matchingBlocks );
			if(!foundores.isEmpty()){
				for (Player p : player.getServer().getOnlinePlayers()) {
					p.sendMessage( plugin.playerMsg( player.getName() + " found " + foundores.size() + " " + getOreColor(block) + getOreNiceName(block) ) );
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
        	alertedBlocks.add(currBlock.getLocation());
//        	System.out.print("Found Block Location: " + currBlock.getLocation().toString());
        	
        	Block xPositive = currBlock.getRelative(1, 0, 0);
            Block xNegative = currBlock.getRelative(-1, 0, 0);
            Block zPositive = currBlock.getRelative(0, 0, 1);
            Block zNegative = currBlock.getRelative(0, 0, -1);
            Block yPositive = currBlock.getRelative(0, 1, 0);
            Block yNegative = currBlock.getRelative(0, -1, 0);

        	if ( !matchingBlocks.contains(xPositive) ) {
        		findNeighborBlocks( type, xPositive, matchingBlocks );
        	}
        	if ( !matchingBlocks.contains(xNegative) ) {
        		findNeighborBlocks( type, xNegative, matchingBlocks );
        	}
        	if ( !matchingBlocks.contains(zPositive) ) {
        		findNeighborBlocks( type, zPositive, matchingBlocks );
        	}
        	if ( !matchingBlocks.contains(zNegative) ) {
        		findNeighborBlocks( type, zNegative, matchingBlocks );
        	}
        	if ( !matchingBlocks.contains(yPositive) ) {
        		findNeighborBlocks( type, yPositive, matchingBlocks );
        	}
        	if ( !matchingBlocks.contains(yNegative) ) {
        		findNeighborBlocks( type, yNegative, matchingBlocks );
        	}
        }
        
        return matchingBlocks;
        
    }
}
