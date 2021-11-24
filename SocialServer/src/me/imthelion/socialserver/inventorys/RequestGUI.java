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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.imthelion.socialserver.Main;
import me.imthelion.socialserver.data.DataManager;
import me.imthelion.socialserver.objects.Request;
import me.imthelion.socialserver.util.ItemUtils;

public class RequestGUI extends GUI implements Listener {
	
	private int page;
	private int maxPage;
	
	public RequestGUI(int page) {
		this.page = page;
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		int i = 0;
		int j = DataManager.getRequests().size();
		while(j>36) {
			i++;
			j-=36;
		}
		maxPage=i;
		if(page>maxPage) {
			throw new IllegalArgumentException("The page number can't be larger than the amount of requests devided by 36!");
		}
		setInventory(createInv());
	}
	
	public boolean update() {
		int i = 0;
		int j = DataManager.getRequests().size();
		while(j>36) {
			i++;
			j-=36;
		}
		maxPage=i;
		if(page>maxPage) {
			return false;
		}
		try {
			List<HumanEntity> vieuwers = INV.getViewers();
				for(int k = 0; k < vieuwers.size(); k++) {
					setInventory(createInv());
					vieuwers.get(k).openInventory(INV);
				}
	} catch(Exception e) {}
		return true;
	}

	@Override
	public Inventory createInv() {
		Inventory inv = Bukkit.createInventory(null, 54, "§9Help request page " + (page+1));
		
		ItemStack item = ItemUtils.getItem(Material.GRAY_STAINED_GLASS_PANE, 1, " ");
		inv.setItem(36, item);
		inv.setItem(37, item);
		inv.setItem(38, item);
		inv.setItem(39, item);
		inv.setItem(40, item);
		inv.setItem(41, item);
		inv.setItem(42, item);
		inv.setItem(43, item);
		inv.setItem(44, item);
		
		inv.setItem(49, ItemUtils.getItem(Material.BARRIER, 1, "§6Close"));
		inv.setItem(48, ItemUtils.getItem(Material.OAK_SIGN, 1, "§aCreate new"));
		inv.setItem(50, ItemUtils.getItem(Material.CHEST, 1, "§bMy Requests"));
		if(page>0) {
			inv.setItem(45, ItemUtils.getItem(Material.ARROW, 1, "9Previous page"));
		}
		if(page < maxPage) {
			inv.setItem(45, ItemUtils.getItem(Material.ARROW, 1, "9Next page"));
		}
		
		for(int i = page*36; i < page*36+36 && i<DataManager.getRequests().size(); i++) {
			inv.setItem(i, DataManager.getRequests().get(i).getGUIItem());
		}
		
		return inv;
	}
	
	@Override
	public void openInv(Player p) {
		InventoryHandler.update();
		p.openInventory(INV);
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory()==null)return;
		if(!e.getInventory().equals(INV)) return;
		
		Player p = (Player) e.getWhoClicked();
		e.setCancelled(true);
		
		if(e.getView().getType() == InventoryType.PLAYER)
			return;
		
		if(e.getSlot()<36) {
			if(DataManager.getRequests().size()-1 < e.getSlot()+(page*36)) {
				InventoryHandler.update();
				return;
			}
			Request req = DataManager.getRequests().get(e.getSlot()+(page*36));
			if(req == null)return;
			if(e.isShiftClick()) {
				if(req.getAmount()-req.getDonated()<10) {
					p.sendMessage("§cThere is not enough space to donate 10!");
					return;
				}
				if(ItemUtils.containsAtLeast(p, req.getMat(), 10)) {
					ItemUtils.removeMaterial(p, req.getMat(), 10);
					req.setDonated(req.getDonated()+10);
					DataManager.setPoints(p, DataManager.getPoints(p)+(req.getPoints()*10));
					
				} else {
					p.sendMessage("§cYou dont have 10 of the requested item!");
					return;
				}
				
				
			} else if(e.isRightClick()) {
				if(ItemUtils.containsAtLeast(p, req.getMat(), req.getAmount()-req.getDonated())) {
					DataManager.setPoints(p, DataManager.getPoints(p)+(req.getPoints()*(req.getAmount()-req.getDonated())));
					ItemUtils.removeMaterial(p, req.getMat(), req.getAmount()-req.getDonated());
					req.setDonated(req.getAmount());
					
				} else {
					p.sendMessage("§cYou dont have enough of the requested item!");
					return;
				}
			} else {
				if(req.getAmount()-req.getDonated()<1) {
					p.sendMessage("§cThe amount of donations is already full!");
					return;
				}
				if(ItemUtils.containsAtLeast(p, req.getMat(), 1)) {
					ItemUtils.removeMaterial(p, req.getMat(), 1);
					req.setDonated(req.getDonated()+1);
					DataManager.setPoints(p, DataManager.getPoints(p)+req.getPoints());
					
				} else {
					p.sendMessage("§cYou dont have any of the requested item!");
					return;
				}
			}
			
			InventoryHandler.update();
			return;
		}
		if(e.getSlot()==45 &&page>0) {
			p.updateInventory();
			InventoryHandler.update();
			InventoryHandler.getGUI(page-1).openInv(p);
			return;
		}
		if(e.getSlot()==53 &&page<maxPage) {
			p.updateInventory();
			InventoryHandler.update();
			InventoryHandler.getGUI(page+1).openInv(p);
			return;
		}
		if(e.getSlot()==49) {
			p.closeInventory();
			p.updateInventory();
			InventoryHandler.update();
			return;
		}
		if(e.getSlot()==48) {
			//create gui
			InventoryHandler.update();
			new CreateGUI(p).openInv(p);
			return;
		}
		
		if(e.getSlot()==50) {
			//open my requests gui
			InventoryHandler.update();
			new MyRequestsGUI(0, p).openInv(p);;
			return;
		}
		
		
		
	}
	
	public int getPage() {
		return page;
	}
	public int getMaxPage() {
		return maxPage;
	}

}
