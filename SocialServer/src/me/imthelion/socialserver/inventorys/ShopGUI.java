package me.imthelion.socialserver.inventorys;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import me.imthelion.socialserver.Main;
import me.imthelion.socialserver.data.DataManager;
import me.imthelion.socialserver.util.ItemUtils;
import me.imthelion.socialserver.util.Lore;
import me.imthelion.socialserver.util.ShopItems;

public class ShopGUI extends GUI implements Listener {
	
	public ShopGUI() {
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		setInventory(createInv());
	}

	@Override
	public Inventory createInv() {
		Inventory inv = Bukkit.createInventory(null, 18, "§9Points Shop");
		inv.setItem(13, ItemUtils.getItem(Material.BARRIER, 1, "§6Close"));
		
		inv.setItem(0, ItemUtils.getItem(Material.HOPPER, 1, "§bDrop Party", null, new Lore().addLine(" ").addLine("§650 points").addLine("  "), false, false, null));
		inv.setItem(2, ItemUtils.getItem(Material.CAKE, 1, "§dGiveaway", null, new Lore().addLine(" ").addLine("§610 points").addLine("  "), false, false, null));
		inv.setItem(4, ItemUtils.getItem(Material.CHEST, 1, "§aAir drop", null, new Lore().addLine(" ").addLine("§625 points").addLine("  "), false, false, null));
		inv.setItem(6, ItemUtils.getItem(Material.TOTEM_OF_UNDYING, 1, "§3Random itemall", null, new Lore().addLine(" ").addLine("§615 points").addLine("  "), false, false, null));
		inv.setItem(8, ItemUtils.getItem(Material.BEDROCK, 1, "§7Random item", null, new Lore().addLine(" ").addLine("§610 points").addLine("  "), false, false, null));
		
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory()==null)return;
		if(!e.getInventory().equals(INV)) return;
		
		Player p = (Player) e.getWhoClicked();
		e.setCancelled(true);
		
		if(e.getView().getType() == InventoryType.PLAYER)
			return;
		
		switch(e.getSlot()) {
		case 13:
			p.closeInventory();
			p.updateInventory();
			break;
		case 0:
			if(DataManager.getPoints(p)<50) {
				p.sendMessage("§cYou can't afford this!");
				break;
			}
			DataManager.setPoints(p, DataManager.getPoints(p)-50);
			ShopItems.dropParty(p);
			p.closeInventory();
			p.updateInventory();
			break;
		case 2:
			if(DataManager.getPoints(p)<10) {
				p.sendMessage("§cYou can't afford this!");
				break;
			}
			DataManager.setPoints(p, DataManager.getPoints(p)-10);
			ShopItems.startGiveaway(ItemUtils.getRandomMaterial(), p);
			p.closeInventory();
			p.updateInventory();
			break;
		case 4:
			if(DataManager.getPoints(p)<25) {
				p.sendMessage("§cYou can't afford this!");
				break;
			}
			DataManager.setPoints(p, DataManager.getPoints(p)-25);
			ShopItems.airDrop(p);
			p.closeInventory();
			p.updateInventory();
			break;
		case 6:
			if(DataManager.getPoints(p)<15) {
			p.sendMessage("§cYou can't afford this!");
			break;
			}
			DataManager.setPoints(p, DataManager.getPoints(p)-15);
			ShopItems.randomItemAll(p);
			p.closeInventory();
			p.updateInventory();
			break;
		case 8:
			if(DataManager.getPoints(p)<10) {
				p.sendMessage("§cYou can't afford this!");
				break;
			}
			DataManager.setPoints(p, DataManager.getPoints(p)-10);
			ShopItems.randomItem(p);
			p.closeInventory();
			p.updateInventory();
			break;
			
		
		}
	}
		

}
