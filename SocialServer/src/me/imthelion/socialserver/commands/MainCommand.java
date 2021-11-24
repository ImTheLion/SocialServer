package me.imthelion.socialserver.commands;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.imthelion.socialserver.Main;
import me.imthelion.socialserver.data.DataManager;
import me.imthelion.socialserver.inventorys.CreateGUI;
import me.imthelion.socialserver.inventorys.InventoryHandler;
import me.imthelion.socialserver.inventorys.MyRequestsGUI;
import me.imthelion.socialserver.util.ShopItems;

public class MainCommand implements CommandExecutor, TabCompleter {
	
	public static String prefix = "§9[§aSocial§2Server§9] §f";
	
	public MainCommand() {
		Main.getInstance().getCommand("SocialServer").setExecutor(this);
		Main.getInstance().getCommand("SocialServer").setTabCompleter(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("SocialServer")) {
			if(args.length>=1) {
				switch(args[0]) {
				case "requests":
					if(!(sender instanceof Player)) {
						sender.sendMessage(prefix +"§cYou have to be a player to perform this command");
						return false;
					}
					InventoryHandler.getGUI(0).openInv((Player)sender);;
					return true;
					
				case "create":
					if(!(sender instanceof Player)) {
						sender.sendMessage(prefix +"§cYou have to be a player to perform this command");
						return false;
					}
					new CreateGUI((Player)sender).openInv((Player)sender);
					return true;
					
				case "myrequests":
					if(!(sender instanceof Player)) {
						sender.sendMessage(prefix +"§cYou have to be a player to perform this command");
						return false;
					}
					new MyRequestsGUI(0,(Player)sender).openInv((Player)sender);;
					return true;
				case "pointall":
					if(sender.hasPermission("socialserver.pointall")) {
						int amount = 10;
						if(args.length >= 2) {
							try {
								amount = Integer.parseInt(args[1]);
							} catch(Exception noNumber) {}
						}
						
						if(Bukkit.getOnlinePlayers().size() <1)return false;
						for(Player p : Bukkit.getOnlinePlayers()) {
							DataManager.setRawPoints(p, DataManager.getRawPoints(p)+amount);
						}
						Bukkit.broadcastMessage(prefix+"§6§l" + sender.getName() + " just did a pointall!");
						return true;
					} 
				case "daily":
					if(!(sender instanceof Player)) {
						sender.sendMessage(prefix +"§cYou have to be a player to perform this command");
						return false;
					}
					if(DataManager.giveDaily((Player)sender)) {
						sender.sendMessage(prefix+"§aYou were given 10 raw points as a daily reward!");
						return true;
					}
					sender.sendMessage(prefix+"§cYou already claimed your daily! Available again in: "
					+ (24-LocalTime.now().getHour()) + " hours, " + (60-LocalTime.now().getMinute() + " minutes."));
					
					return true;
					
				case "entergiveaway":
					if(!(sender instanceof Player)) {
						sender.sendMessage(prefix +"§cYou have to be a player to perform this command");
						return false;
					}
					
					if(ShopItems.runningGiveaway) {
						ShopItems.giveawayEnters.add(((Player)sender).getUniqueId());
						sender.sendMessage(prefix+"§6You succesfully entered the giveaway!");
						return true;
					}
					
				case "shop":
					if(!(sender instanceof Player)) {
						sender.sendMessage(prefix +"§cYou have to be a player to perform this command");
						return false;
					}
					InventoryHandler.shopgui.openInv((Player)sender);
					return true;
				case "help":
					sender.sendMessage("§9----------- " + prefix+ "§9-------------");
					sender.sendMessage("§a/socialserver requests§9 -> §bOpen the requests gui.");
					sender.sendMessage("§a/socialserver myrequests§9 -> §bOpen the gui with all your requests.");
					sender.sendMessage("§a/socialserver create§9 -> §bOpen the gui where you can create a new Request.");
					sender.sendMessage("§a/socialserver shop§9 -> §bOpen the shop where you can spend your points.");
					sender.sendMessage("§a/socialserver daily§9 -> §bClaim your daily reward, 10 raw points.");
					if(sender.hasPermission("socialserver.pointall")) {
						sender.sendMessage("§a/socialserver pointall§9 -> §bDo a pointall, give every player on the server a specified number or 10 points");
					}
					return true;
				}
				
				
			}
			
			
			
			sender.sendMessage(prefix+"Running SocialServer v" + Main.getInstance().getDescription().getVersion());
		}
		
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] args) {
		List<String> list = new ArrayList<>();
		if(args.length == 1) {
			list.add("requests");
			list.add("create");
			list.add("myrequests");
			list.add("daily");
			list.add("shop");
			list.add("help");
			if(arg0.hasPermission("socialserver.admin")) {
				list.add("pointall");
			}
			List<String> result = new ArrayList<String>();
			for(String a : list) {
				if(a.toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(a);
			}
			return result;
		}
		return list;
	}

}
