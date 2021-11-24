package me.imthelion.socialserver.inventorys;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class ActionConfirmEvent extends Event {

private static final HandlerList HANDLERS = new HandlerList();
private final Player player;
private Inventory inv;
private final boolean confirmed;
private final Object o;
	
	public ActionConfirmEvent(Player player, Inventory inv, Object data, boolean confirmed) {
		this.player = player;
		this.inv = inv;
		this.confirmed = confirmed;
		this.o = data;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Inventory getInv() {
		return inv;
	}
	
	public Object getData() {
		return o;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
	
	public boolean confirmed() {
		return confirmed;
	}


}
