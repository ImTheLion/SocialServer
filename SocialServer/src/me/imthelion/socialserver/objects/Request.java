package me.imthelion.socialserver.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.imthelion.socialserver.Main;
import me.imthelion.socialserver.data.DataManager;
import me.imthelion.socialserver.util.ItemUtils;
import me.imthelion.socialserver.util.Lore;

public class Request {
	
	private OfflinePlayer p;
	private Material mat;
	private int amount;
	private int points;
	private int donated;
	private UUID id;
	
	public Request(UUID owner, Material mat, int amount, int points, int donated) {
		this(owner, mat, amount, points, donated, UUID.randomUUID());
	}
	
	public Request(UUID owner, Material mat, int amount, int points, int donated, UUID id) {
		if(Bukkit.getOfflinePlayers().length < 1) {
			this.setP(null);
			this.setMat(mat);
			this.setAmount(amount);
			this.setPoints(points);
			this.setDonated(donated);
			return;
		}
		for(OfflinePlayer p : Bukkit.getOfflinePlayers()) {
			if(p.getUniqueId().equals(owner)) {
				this.setP(p);
				this.setMat(mat);
				this.setAmount(amount);
				this.setPoints(points);
				this.setDonated(donated);
				return;
			}
		}
		this.setP(null);
		this.setMat(mat);
		this.setAmount(amount);
		this.setPoints(points);
		this.setDonated(donated);
	}
	
	public Request(OfflinePlayer p, Material mat, int amount, int points, int donated) {
		this(p, mat, amount, points, donated, UUID.randomUUID());
	}
	
	public Request(OfflinePlayer p, Material mat, int amount, int points, int donated, UUID id) {
		this.setP(p);
		this.setMat(mat);
		this.setAmount(amount);
		this.setPoints(points);
		this.setDonated(donated);
		this.setId(id);
	}
	
	public ItemStack getGUIItem() {
		Lore lore = new Lore();
		lore.addLine(" ").addLine("§5Request for: §d" + getMat().toString()).addLine("§9Owned by: §2" + getP().getName()).addLine("§6Reward: §e" + getPoints() + " points").addLine("  ")
		.addLine("§bProgress: §f" + donated + "/" + amount).addLine("   ").addLine("§aClick to donate 1").addLine("§aRight click to donate to max").addLine("§aShift click to donate 10").addLine("    ");
		ItemStack item = ItemUtils.getItem(getMat(), 1, "§6"+getMat().toString(), null, lore, false, true, 0);
		return item;
	}
	
	public ItemStack getMyGUIItem() {
		Lore lore = new Lore();
		if(donated >= amount) {
			lore.addLine(" ").addLine("§5Request for: §d" + getMat().toString()).addLine("§6Amount: §e" + getAmount()).addLine("  ")
			.addLine("§bProgress: §fDONE").addLine("   ").addLine("§aClick to collect").addLine("    ");
		} else {
			lore.addLine(" ").addLine("§5Request for: §d" + getMat().toString()).addLine("§6Reward: §e" + getPoints() + " points").addLine("  ")
			.addLine("§bProgress: §f" + donated + "/" + amount).addLine("   ").addLine("§aClick to delete and collect").addLine("    ");
		}
		ItemStack item = ItemUtils.getItem(getMat(), 1, "§6"+getMat().toString(), null, lore, false, true, 0);
		return item;
	}
	
	public boolean isDone() {
		return donated >=amount;
	}
	
	public boolean delete() {
		if(DataManager.removeRequest(this)) {
			this.p = null;
			this.amount = -1;
			this.donated = -1;
			this.id = null;
			this.mat = null;
			return true;
		}
		return false;
	}
	
	public static List<Request> loadFromconfig() {
		ConfigurationSection section = Main.getInstance().getRequests().getConfigurationSection("requests");
		if(section == null ) return new ArrayList<Request>();
		List<Request> requests = new ArrayList<>();
		for(String id : section.getKeys(false)) {
			requests.add(load(UUID.fromString(id)));
		}
		return requests;
	}
	
	public static Request load(UUID id) {
		FileConfiguration data = Main.getInstance().getRequests();
		OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(data.getString("requests."+id+".owner.uuid")));
		Material mat = Material.getMaterial(data.getString("requests."+id+".material"));
		int amount = Integer.parseInt(data.getString("requests."+id+".amount"));
		int progress = Integer.parseInt(data.getString("requests."+id+".progress"));
		int points = Integer.parseInt(data.getString("requests."+id+".reward"));
		
		return new Request(player, mat, amount, points, progress);
	}
	
	public static void saveAll(List<Request> requests) {
		FileConfiguration data = Main.getInstance().getRequests();
		data.set("requests", null);
		for(Request r : requests) {
			UUID id = r.getId();
			data.set("requests."+id+".owner.uuid", r.getP().getUniqueId().toString());
			data.set("requests."+id+".owner.name", r.getP().getName());
			data.set("requests."+id+".material", r.getMat().toString());
			data.set("requests."+id+".amount", r.getAmount());
			data.set("requests."+id+".progress", r.getDonated());
			data.set("requests."+id+".reward", r.getPoints());
			Main.getInstance().saveRequests();
		}
	}

	public Material getMat() {
		return mat;
	}

	public void setMat(Material mat) {
		this.mat = mat;
	}

	public OfflinePlayer getP() {
		return p;
	}

	public void setP(OfflinePlayer p) {
		this.p = p;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getDonated() {
		return donated;
	}

	public void setDonated(int donated) {
		this.donated = donated;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
	public boolean isOwnedBy(OfflinePlayer player) {
		return p.equals(player);
	}
	

}
