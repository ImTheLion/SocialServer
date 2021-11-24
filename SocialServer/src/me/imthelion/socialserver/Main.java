package me.imthelion.socialserver;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.imthelion.socialserver.commands.MainCommand;
import me.imthelion.socialserver.data.DataManager;
import me.imthelion.socialserver.data.PlayerData;
import me.imthelion.socialserver.data.RequestData;
import me.imthelion.socialserver.inventorys.InventoryHandler;
import me.imthelion.socialserver.util.ShopItems;

public class Main extends JavaPlugin {
	
	private static Main instance;
	private RequestData requests;
	private PlayerData data;
	
	@Override
	public void onEnable() {
		instance = this;
		this.saveDefaultConfig();
		requests = new RequestData(this);
		data = new PlayerData(this);
		
		DataManager.LoadAll();
		InventoryHandler.Load();
		
		new MainCommand();
		Bukkit.getPluginManager().registerEvents(new ShopItems(), this);
	}
	
	
	@Override
	public void onDisable() {
		DataManager.saveRequests();
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public FileConfiguration getRequests() {
		return requests.getConfig();
	}
	
	public void saveRequests() {
		requests.saveConfig();
	}
	public void reloadRequests() {
		requests.reloadConfig();
	}
	
	public FileConfiguration getData() {
		return data.getConfig();
	}
	
	public void saveData() {
		data.saveConfig();
	}
	public void reloadData() {
		data.reloadConfig();
	}
	
	
	
}
