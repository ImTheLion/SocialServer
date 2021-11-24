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
import me.imthelion.socialserver.util.ItemUtils;

public class Confirm implements Listener{
	
	private Inventory inv;
	private final Inventory previous;
	private final Object o;
	
	public Confirm(Inventory previous, Object data) {
		this.previous = previous;
		o = data;
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		createInventory();
	}
	
	public void openInventory(Player p) {		
		p.openInventory(inv);
	}
	
	public void createInventory() {
		Inventory inv = Bukkit.createInventory(null, InventoryType.HOPPER, "§6Are you sure?");
		
		inv.setItem(0, ItemUtils.getItem(Material.GREEN_WOOL, 1, "§aYes"));
		inv.setItem(4, ItemUtils.getItem(Material.RED_WOOL, 1, "§cCancel"));
		this.inv = inv;
	}
	
	public Inventory getInv() {
		return inv;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(e.getInventory()==null)return;
		if(!e.getInventory().equals(inv)) return;
		
		Player p = (Player) e.getWhoClicked();
		e.setCancelled(true);
		
		if(e.getView().getType() == InventoryType.PLAYER)
			return;
		if(e.getSlot() == 0) {
			Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {

				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new ActionConfirmEvent(p, previous, o, true));
					
				}
				
			});
			return;
		} else if(e.getSlot() == 4) {
			Bukkit.getScheduler().runTask(Main.getInstance(), new Runnable() {

				@Override
				public void run() {
					Bukkit.getPluginManager().callEvent(new ActionConfirmEvent(p, previous, o, false));
					
				}
				
			});
			return;
		}
	}
	
	

}
