package com.gmail.risen619.easytree;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

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
	public void onBlockBreak(BlockBreakEvent event)
	{
		if(plugin.exceptions.contains(event.getPlayer().getName()))
			return;
		
		Block broken = event.getBlock();
		if(wood.contains(broken.getType()))
		{
			boolean isTree = false;
			ArrayList<Block> blocksToDestroy = new ArrayList<Block>();
			Block tmp = broken;
			String info = "[";
			
			while(wood.contains(tmp.getRelative(BlockFace.DOWN, 1).getType()))
				tmp = tmp.getRelative(BlockFace.DOWN, 1);
			info += tmp.getY() + ";";
			
			do
			{
				blocksToDestroy.add(tmp);
				ArrayList<Block> branches = getBranchesAround(tmp);
				if(branches != null)
					blocksToDestroy.addAll(branches);
				if(hasLeavesAround(tmp))
					isTree = true;
				
				tmp = tmp.getRelative(BlockFace.UP, 1);
			}while(wood.contains(tmp.getType()));
			info += tmp.getY();
			
			plugin.getLogger().info(info + "] - " + isTree);
			
			if(isTree)
				for(Block b : blocksToDestroy)
					b.breakNaturally();
		}
	}
	
	private ArrayList<Block> getBranchesAround(Block b)
	{
		ArrayList<Block> t = new ArrayList<Block>();
		int cx, cz;
		cx = b.getX(); cz = b.getZ();
		
		for(int i = cx; i<=cx + 3; i++)
			for(int j=cz; j<=cz + 3; j++)
			{
				Block tmp = b.getLocation().add(i, 0, j).getBlock();
				if(wood.contains(tmp.getType()))
					t.add(tmp);
			}
		
		return t;
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
