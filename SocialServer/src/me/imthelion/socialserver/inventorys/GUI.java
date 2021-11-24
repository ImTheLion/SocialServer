package me.imthelion.socialserver.inventorys;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class GUI {
	
	protected Inventory INV;
	
	public GUI() {
	}
	
	public abstract Inventory createInv();
	
	public void openInv(Player p) {
		p.openInventory(INV);
	}
	
	public void setInventory(Inventory inv) {
		INV = inv;
	}
	
	public Inventory getInv() {
		return INV;
	}

}
