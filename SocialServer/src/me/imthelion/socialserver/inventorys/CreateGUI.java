package me.imthelion.socialserver.inventorys;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

import me.imthelion.socialserver.Main;
import me.imthelion.socialserver.data.DataManager;
import me.imthelion.socialserver.objects.Request;
import me.imthelion.socialserver.util.ItemUtils;
import me.imthelion.socialserver.util.Lore;

public class CreateGUI extends GUI implements Listener {
	
	private int points= 10;
	private int amount = 1;
	private Material mat;
	private Player owner;
	private boolean material = false;
	
	public CreateGUI(Player p) {
		this.owner =p;
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		setInventory(createInv());
	}

	@Override
	public Inventory createInv() {
		Inventory inv = Bukkit.createInventory(null, 54, "§9Create Request");
		
		if(points > 1)
			inv.setItem(21, ItemUtils.getItem(Material.ARROW, 1, "One Less"));
		if(DataManager.getRawPoints(owner)>points)
			inv.setItem(23, ItemUtils.getItem(Material.ARROW, 1, "One More"));
		inv.setItem(22, ItemUtils.getItem(Material.SUNFLOWER, points>64?1:points, "§5Points: §d" + points));
			
		if(amount > 1)
			inv.setItem(30, ItemUtils.getItem(Material.ARROW, 1, "One Less"));
		if(amount<64)
			inv.setItem(32, ItemUtils.getItem(Material.ARROW, 1, "One More"));
		inv.setItem(31, ItemUtils.getItem(Material.MAGENTA_GLAZED_TERRACOTTA, amount>64?1:amount, "§2Amount: §a" + amount));

		
		Lore lore = new Lore();
		lore.addLine(" ").addLine("§5Points: §d"+ points).addLine("§2Amount: §a"+amount).addLine("  ").addLine("§7Click to set Type").addLine("    ");
		inv.setItem(13, ItemUtils.getItem(mat==null?Material.STRUCTURE_VOID:mat, amount, "§6Current Request:", null, lore, false, false, 0));
		
		
		inv.setItem(49, ItemUtils.getItem(Material.BARRIER, 1, "§cCancel"));
		inv.setItem(48, ItemUtils.getItem(Material.LIME_DYE, 1, "§aCreate"));
		
		return inv;
	}
	
	public void update() {
		try {
				List<HumanEntity> vieuwers = INV.getViewers();
					INV = createInv();
					for(int i = 0; i < vieuwers.size(); i++) {
						vieuwers.get(i).openInventory(INV);
					}
		} catch(Exception e) {}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(!e.getPlayer().equals(owner)) return;
		if(!material) return;
		e.setCancelled(true);
		try {
			mat = Material.matchMaterial(e.getMessage());
		} catch(Exception invailid) {
			owner.sendMessage("§cInvaild Material! Example: " + Material.DIAMOND_ORE.toString());
			Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
				
				@Override
				public void run() {
					openInv(owner);
				}
			});
			material = false;
			return;
		}
		if(mat == null)owner.sendMessage("§cInvaild Material! Example: " + Material.DIAMOND_ORE.toString());
		Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				openInv(owner);
			}
		});
		material = false;
		update();
		
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory()==null)return;
		if(!e.getInventory().equals(INV)) return;
		
		Player p = (Player) e.getWhoClicked();
		e.setCancelled(true);
		
		if(e.getView().getType() == InventoryType.PLAYER)
			return;
		
		if(e.getCurrentItem()==null)return;
		
		if(e.getSlot() == 49) {
			p.closeInventory();
			p.updateInventory();
		}
		if(e.getSlot() == 48) {
			if(mat == null) {
				p.sendMessage("§cThere is no material provided!");
				return;
			}
			DataManager.addRequest(new Request(p, mat, amount, Math.round(points/amount), 0));
			DataManager.setRawPoints(p, DataManager.getRawPoints(p)-points);
			p.sendMessage("§aCreated a new Requests, type §b/socialserver myrequests§a to vieuw all your requests!");
			p.closeInventory();
			p.updateInventory();
		}
		if(e.getSlot() == 13) {
			material = true;
			p.sendMessage("§aType the material in chat. Example: " + Material.NETHER_BRICK_FENCE.toString());
			p.closeInventory();
		}
		
		if(e.getSlot() == 30) {
			if(amount > 1)
				amount--;
		}
		
		if(e.getSlot() == 32) {
			if(amount<64)
				amount++;
		}
		
		if(e.getSlot() == 21) {
			if(points > 1)
				points--;
		}
		if(e.getSlot() == 23) {
			if(DataManager.getRawPoints(owner)>points)
				points++;
		}
		
		
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::update, 1);
	}

}
