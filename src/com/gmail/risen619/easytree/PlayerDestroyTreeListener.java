package com.gmail.risen619.easytree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDestroyTreeListener implements Listener {

	private Main plugin;
	private ArrayList<Material> wood;
	private ArrayList<Material> leaves;
	
	public PlayerDestroyTreeListener(Main plugin)
	{
		this.plugin = plugin;
		wood = new ArrayList<Material>();
		leaves = new ArrayList<Material>();
		
		wood.add(Material.LOG);
		wood.add(Material.LOG_2);

		leaves.add(Material.LEAVES);
		leaves.add(Material.LEAVES_2);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player p = event.getPlayer();
		ItemStack is = p.getInventory().getItemInMainHand();
		if(is.getType() == Material.BLAZE_ROD && event.getClickedBlock() instanceof Block)
		{
			Block b = event.getClickedBlock();
			plugin.getLogger().info(b.getX() + ";" + b.getY() + ";" + b.getZ());
		}
	}
	
	@EventHandler
 	public void onBlockBreak(BlockBreakEvent event)
	{
		if(plugin.exceptions.contains(event.getPlayer().getName()))
			return;
		
		Block broken = event.getBlock();
		if(wood.contains(broken.getType()))
		{
			boolean isTree = false;
			HashSet<Block> blocksToDestroy = new HashSet<Block>();
			Block tmp = broken;
			HashSet<Block> set = new HashSet<Block>();
			HashMap<Double, Block> dist = new HashMap<Double, Block>();
			
			while(true)
			{
				HashSet<Block> temporaryBlocks = woodInArea(tmp.getLocation(), 0, 2);
				if(temporaryBlocks == null || temporaryBlocks.isEmpty())
					break;
				
				set = temporaryBlocks;
				tmp = tmp.getRelative(BlockFace.DOWN, 1);
			}
			plugin.getLogger().info(new Integer(set.size()).toString());
			for(Block b : set)
				dist.put(b.getLocation().distance(broken.getLocation()), b);
			tmp = getCenter(dist, broken.getLocation());
			
			while(!woodInArea(tmp.getLocation(), 0, 1).isEmpty())
			{
				set = woodInArea(tmp.getLocation(), 0, 2);
				if(set.isEmpty())
					break;
				
				blocksToDestroy.addAll(set);
				if(hasLeavesAround(tmp))
					isTree = true;
				
				tmp = tmp.getRelative(BlockFace.UP, 1);
			}
			
			if(isTree)
				for(Block b : blocksToDestroy)
					b.breakNaturally();
		}
	}
	
	private Block getCenter(HashMap<Double, Block> map, Location center)
	{
		Double[] dist = new Double[map.size()];
		dist = map.keySet().toArray(dist);
		Double min = dist[0];
		for(int i=1; i<dist.length; i++)
			if(dist[i] < min)
				min = dist[i];
		
		return map.get(min);
	}

	private HashSet<Block> woodInArea(Location c, int o, int p)
	{
		HashSet<Block> blocks = new HashSet<Block>();
		
		if(o > 9)
			return blocks;
		
		int xmin, xmax, zmin, zmax;
		xmin = c.getBlockX()-o;
		xmax = c.getBlockX()+o;
		zmin = c.getBlockZ()-o;
		zmax = c.getBlockZ()+o;
		
		for(int i=xmin; i<=xmax; i++)
			for(int j=zmin; j<=zmax; j++)
				if(!(i > xmin && i < xmax && j > zmin && j < zmax))
				{
					Block b = c.getBlock().getWorld().getBlockAt(i, c.getBlockY(), j);
					if(wood.contains(b.getType()))
						blocks.add(b);
				}

		plugin.getLogger().info("Func: " + blocks.size() + ":" + o + ";" + p);
		if(o < p || blocks.size() > 0)
			blocks.addAll(woodInArea(c,o+1,p));
		
		return blocks;
	}
	
	private boolean hasLeavesAround(Block b)
	{
		return (leaves.contains(b.getRelative(BlockFace.WEST, 1).getType())) ||
			(	leaves.contains(b.getRelative(BlockFace.EAST, 1).getType())) ||
			(	leaves.contains(b.getRelative(BlockFace.NORTH, 1).getType())) ||
			(	leaves.contains(b.getRelative(BlockFace.SOUTH, 1).getType())) ||
			(	leaves.contains(b.getRelative(BlockFace.SOUTH_EAST, 1).getType())) ||
			(	leaves.contains(b.getRelative(BlockFace.SOUTH_WEST, 1).getType())) ||
			(	leaves.contains(b.getRelative(BlockFace.NORTH_EAST, 1).getType())) ||
			(	leaves.contains(b.getRelative(BlockFace.NORTH_WEST, 1).getType())) ||
			(	leaves.contains(b.getRelative(BlockFace.UP, 1).getType()));
			
	}
	
}
