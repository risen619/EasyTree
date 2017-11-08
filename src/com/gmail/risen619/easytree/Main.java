package com.gmail.risen619.easytree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public ArrayList<String> exceptions;
	
	private FileConfiguration exceptionsConfig;
	private File exceptionsFile;
	
	@Override
	public void onEnable() {
		exceptions = new ArrayList<String>();
		exceptionsFile = new File(getDataFolder(), "exceptions.yml");
		exceptionsConfig = YamlConfiguration.loadConfiguration(exceptionsFile);

		exceptions.addAll(exceptionsConfig.getStringList("Players.Exceptions"));	
		
		getServer().getPluginManager().registerEvents(new PlayerDestroyTreeListener(this), this);
		
		getLogger().info("EasyTree enabled.");
	}
	
	@Override
	public void onDisable() {

		exceptionsConfig.set("Players.Exceptions", exceptions);
		try { exceptionsConfig.save(exceptionsFile); }
		catch (IOException e) { e.printStackTrace(); }
		
		getLogger().info("EasyTree disabled.");
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
		
		if(s instanceof Player && c.getName().startsWith("et"))
		{
			Player p = (Player)s;
			switch(c.getName().toLowerCase())
			{
				case "etdisable": return addException(p);
				case "etenable": return removeException(p);
				default: return false;
			}
		}
		
		return false;
	}
	
	private boolean addException(Player p)
	{
		exceptions.add(p.getName());
		return true;
	}
	
	private boolean removeException(Player p)
	{
		exceptions.remove(p.getName());
		return true;
	}
	
}
