package me.imthelion.socialserver.inventorys;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import me.imthelion.socialserver.Main;

public class InventoryHandler {
	
	public static List<RequestGUI> guis;
	public static ShopGUI shopgui= new ShopGUI();
	
	
	
	public static void Load() {
		//request GUI's
		guis = new ArrayList<>();
		boolean a = true;
		for(int i = 0; a; i++) {
			RequestGUI gui = possible(i);
			if(gui!=null) {
				guis.add(gui);
				continue;
			}
			a=false;
		}
		//next
		
		
		
		
	}
	
	
	
	private static RequestGUI possible(int i) {
		try {
			return new RequestGUI(i);
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
	
	public static RequestGUI getGUI(int page) {
		for(RequestGUI gui : guis) {
			if(gui.getPage()==page)return gui;
		}
		return null;
		
	}
	
	public static void update() {
		Bukkit.getScheduler().runTaskLater(Main.getInstance(),new Runnable() {
			
			
			@Override
			public void run() {
				int currentMaxPage = 0;
				boolean a = true;
				for(RequestGUI gui : guis) {
					if(!gui.update()) {
						guis.remove(gui);
						continue;
					} else {
						if(gui.getPage()>currentMaxPage)currentMaxPage=gui.getPage();
					}
				}
				for(int i = currentMaxPage; a; i++) {
					RequestGUI gui = possible(i);
					if(gui!=null) {
						guis.add(gui);
						continue;
					}
					a=false;
				}
				for(RequestGUI gui : guis) {
					gui.update();
				}
			}
		}, 1);
	}

}
