package me.imthelion.socialserver.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.imthelion.socialserver.Main;

public class PlayerData {
	
	private Main main;
	private FileConfiguration dataConfig = null;
	private File configFile = null;
	
	public PlayerData(Main main) {
		this.main = main;
		this.saveDefaultConfig();
	}
	
	public void reloadConfig() {
		if(this.configFile == null) {
			this.configFile = new File(this.main.getDataFolder(), "data.yml");
		}
		this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
		
		InputStream defaultStream = this.main.getResource("data.yml");
		if(defaultStream != null) {
			YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
			this.dataConfig.setDefaults(defaultConfig);
		}
	}
	
	public FileConfiguration getConfig() {
		if(this.dataConfig == null) {
			this.reloadConfig();
		}
		return this.dataConfig;
	}
	
	public void saveConfig() {
		if(this.dataConfig == null || this.configFile == null) {
			return;
		}
		
		try {
			this.getConfig().save(this.configFile);
		} catch	(IOException e){
			main.getLogger().log(Level.SEVERE, "Could not load config to " + this.configFile, e);
		}
	}
	
	public void saveDefaultConfig() {
		if(this.configFile == null) {
			this.configFile = new File(this.main.getDataFolder(), "data.yml");
		}
		try {
			if(this.configFile.exists()) {
				this.main.saveResource("data.yml", false);
			}
		} catch(Exception e) {
			
		}
	}

}
