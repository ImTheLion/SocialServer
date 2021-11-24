package me.imthelion.socialserver.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.imthelion.socialserver.Main;
import me.imthelion.socialserver.objects.DailyTime;
import me.imthelion.socialserver.objects.Request;

public class DataManager {
	
	private static List<Request> requests;
	private static Map<UUID, Integer> rawPoints;
	private static Map<UUID, Integer> points;
	private static Map<UUID, DailyTime> dailyTime;
	
	public static void saveRequests() {
		Request.saveAll(requests);
		requests.clear();
		saveAllPoints();
	}
	
	public static void LoadAll() {
		requests = Request.loadFromconfig();
		rawPoints = loadRawFromConfig();
		points = loadFromConfig();
		dailyTime = loadDaily();
	}
	
	public static List<Request> getRequests() {
		return requests;
	}
	
	public static void addRequest(Request req) {
		requests.add(req);
	}
	
	public static int getPoints(OfflinePlayer p) {
		return getPoints(p.getUniqueId());
	}
	public static int getPoints(UUID id) {
		return points.containsKey(id)?points.get(id):0;
	}
	
	public static int getRawPoints(OfflinePlayer p) {
		return getRawPoints(p.getUniqueId());
	}
	public static int getRawPoints(UUID id) {
		return rawPoints.containsKey(id)?rawPoints.get(id):0;
	}
	
	public static void setPoints(OfflinePlayer p, int value) {
		setPoints(p.getUniqueId(), value);
	}
	public static void setPoints(UUID id, int value) {
		points.put(id, value);
	}
	
	public static void setRawPoints(OfflinePlayer p, int value) {
		setRawPoints(p.getUniqueId(), value);
	}
	public static void setRawPoints(UUID id, int value) {
		rawPoints.put(id, value);
	}
	
	public static boolean removeRequest(Request req) {
		try {
			requests.remove(req);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static int getOwnedAmount(OfflinePlayer p) {
		int i = 0;
		for(Request req: requests) {
			if(req.isOwnedBy(p))i++;
		}
		return i;
	}
	
	public static Request getOwnedRequest(OfflinePlayer p, int index) {
		List<Request> r = new ArrayList<>();
		for(Request req: requests) {
			if(req.isOwnedBy(p))r.add(req);
		}
		return r.size()<=0?null:r.get(index);
	}
	
	public static Map<UUID, Integer> loadRawFromConfig() {
		ConfigurationSection section = Main.getInstance().getData().getConfigurationSection("data");
		if(section == null ) return new HashMap<UUID, Integer>();
		Map<UUID, Integer> rawPoints = new HashMap<>();
		for(String id : section.getKeys(false)) {
			rawPoints.put(UUID.fromString(id), loadRaw(UUID.fromString(id)));
		}
		return rawPoints;
	}
	
	public static int loadRaw(UUID id) {
		FileConfiguration data = Main.getInstance().getData();
		int amount = data.getInt("data."+id+".rawPoints");
		return amount;
	}
	
	public static Map<UUID, Integer> loadFromConfig() {
		ConfigurationSection section = Main.getInstance().getData().getConfigurationSection("data");
		if(section == null ) return new HashMap<UUID, Integer>();
		Map<UUID, Integer> rawPoints = new HashMap<>();
		for(String id : section.getKeys(false)) {
			rawPoints.put(UUID.fromString(id), load(UUID.fromString(id)));
		}
		return rawPoints;
	}
	
	public static int load(UUID id) {
		FileConfiguration data = Main.getInstance().getData();
		int amount = data.getInt("data."+id+".points");
		return amount;
	}
	
	public static void saveAllPoints() {
		FileConfiguration data = Main.getInstance().getData();
		data.set("data", null);
		for(Entry<UUID, Integer> e : rawPoints.entrySet()) {
			UUID id = e.getKey();
			data.set("data."+id+".name", Bukkit.getOfflinePlayer(id).getName());
			data.set("data."+id+".rawPoints", e.getValue());
			Main.getInstance().saveData();
		
		}
		for(Entry<UUID, Integer> e : points.entrySet()) {
			UUID id = e.getKey();
			data.set("data."+id+".name", Bukkit.getOfflinePlayer(id).getName());
			data.set("data."+id+".points", e.getValue());
			Main.getInstance().saveData();
		}
		for(Entry<UUID, DailyTime> e : dailyTime.entrySet()) {
			data.set("data." + e.getKey() + ".daily", e.getValue().getDay() + "/" + e.getValue().getMonth() + "/" + e.getValue().getYear());
			Main.getInstance().saveData();
		}
		
	}
	
	public static Map<UUID, DailyTime> loadDaily() {
		ConfigurationSection section = Main.getInstance().getData().getConfigurationSection("data");
		if(section == null ) return new HashMap<UUID, DailyTime>();
		Map<UUID, DailyTime> date = new HashMap<>();
		for(String id : section.getKeys(false)) {
			date.put(UUID.fromString(id), loadDaily(UUID.fromString(id)));
		}
		return date;
	}
	
	public static DailyTime loadDaily(UUID id) {
		FileConfiguration data = Main.getInstance().getData();
		String date = data.getString("data."+id+".daily");
		String[] dmy = date.split("/");
		short day;
		short month;
		int year;
		try {
			day = Short.parseShort(dmy[0]);
			month = Short.parseShort(dmy[1]);
			year = Integer.parseInt(dmy[2]);
		} catch(Exception e) {
			day = 0;
			month = 0;
			year = 0;
		}
		
		
		return new DailyTime(day, month, year);
	}
	
	public static boolean giveDaily(Player p) {
		if(!dailyTime.containsKey(p.getUniqueId())) {
			setRawPoints(p, getRawPoints(p)+10);
			dailyTime.put(p.getUniqueId(), DailyTime.now());
			return true;
		}
		if(DailyTime.now().isLater(dailyTime.get(p.getUniqueId()))) {
			setRawPoints(p, getRawPoints(p)+10);
			dailyTime.put(p.getUniqueId(), DailyTime.now());
			return true;
		}
		return false;
	}
	

}
