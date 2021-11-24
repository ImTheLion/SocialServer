package me.imthelion.socialserver.inventorys;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

public class MyRequestsGUI extends GUI implements Listener{

	private int page;
	private int maxPage;
	private OfflinePlayer owner;
	
	public MyRequestsGUI(int page, Player p) {
		this.page = page;
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		int i = 0;
		int j = DataManager.getOwnedAmount(p);
		while(j>36) {
			i++;
			j-=36;
		}
		maxPage=i;
		if(page>maxPage) {
			throw new IllegalArgumentException("The page number can't be larger than the amount of owned requests devided by 36!");
		}
		owner = p;
		this.INV = createInv();
	}
	
	public boolean update() {
		int i = 0;
		int j = DataManager.getOwnedAmount(owner);
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
					this.INV=createInv();
					vieuwers.get(i).openInventory(INV);
				}
	} catch(Exception e) {}
		return true;
	}

	@Override
	public Inventory createInv() {
		Inventory inv = Bukkit.createInventory(null, 54, "§9Your requests page " + (page+1));
		
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
		inv.setItem(50, ItemUtils.getItem(Material.CHEST, 1, "§bAll Requests"));
		if(page>0) {
			inv.setItem(45, ItemUtils.getItem(Material.ARROW, 1, "9Previous page"));
		}
		if(page < maxPage) {
			inv.setItem(45, ItemUtils.getItem(Material.ARROW, 1, "9Next page"));
		}
		
		for(int i = page*36; i < page*36+36 && i<DataManager.getOwnedAmount(owner); i++) {
			inv.setItem(i, DataManager.getOwnedRequest(owner, i).getMyGUIItem());
		}
		
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
		
		if(e.getSlot()<36) {
			if(DataManager.getOwnedAmount(owner)-1 < e.getSlot()+(page*36)) {
				InventoryHandler.update();
				return;
			}
			Request r = DataManager.getOwnedRequest(owner, e.getSlot()+(page*36));
			if(r == null)return;
			if(r.isDone()) {
				if(r.getP() instanceof Player) {
					try {
						Material mat = r.getMat();
						int i = r.getDonated();
						if(r.delete())
							p.getInventory().addItem(new ItemStack(mat, i));
						update();
					}catch(Exception noSpace) {}
				}
			} else {
				new Confirm(INV, r).openInventory(p);
			}
			return;
		}
		if(e.getSlot()==45 &&page>0) {
			p.updateInventory();
			InventoryHandler.getGUI(page-1).openInv(p);
			InventoryHandler.update();
			return;
		}
		if(e.getSlot()==53 &&page<maxPage) {
			p.updateInventory();
			InventoryHandler.getGUI(page+1).openInv(p);
			InventoryHandler.update();
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
			new CreateGUI(p).openInv(p);
			InventoryHandler.update();
			return;
		}
		
		if(e.getSlot()==50) {
			//open normal requests gui
			this.update();
			InventoryHandler.getGUI(0).openInv(p);
			InventoryHandler.update();
			return;
		}
		
		
		
	}
	
	@EventHandler
	public void onAction(ActionConfirmEvent e) {
		if(!e.getPlayer().equals(owner))return;
		if(!e.getInv().equals(INV))return;
		if(e.getData() instanceof Request) {
			Request r = (Request) e.getData();
			if(r.getP() instanceof Player) {
				Player p = (Player) r.getP();
				if(!e.confirmed()) {update();p.openInventory(INV);return;}
				try {
					Material mat = r.getMat();
					int i = r.getDonated();
					if(r.delete())
						p.getInventory().addItem(new ItemStack(mat, i));
				}catch(Exception noSpace) {}
				update();
				p.openInventory(INV);
			}
		}
	}
	
	public int getPage() {
		return page;
	}
	public int getMaxPage() {
		return maxPage;
	}

}
