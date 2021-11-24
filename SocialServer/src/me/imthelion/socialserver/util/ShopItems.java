package me.imthelion.socialserver.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.imthelion.socialserver.Main;
import me.imthelion.socialserver.commands.MainCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ShopItems implements Listener {
	
	public static Random r = new Random();
	
	public static boolean runningGiveaway = false;
	public static List<UUID> giveawayEnters = new ArrayList<>(); 
	
	public static void startGiveaway(Material mat, Player p) {
		TextComponent comp = new TextComponent(MainCommand.prefix + "§6§l" + p.getName() + " is hosting a giveaway, CLICK to enter");
		comp.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/socialserver entergiveaway"));
		comp.setColor(ChatColor.GOLD);
		comp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
				new ComponentBuilder("Click to enter the giveaway").color(ChatColor.GOLD).create()));
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.spigot().sendMessage(comp);
		}
		
		runningGiveaway = true;
		
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				
				int i = r.nextInt(giveawayEnters.size());
				OfflinePlayer player;
				do {
					player = Bukkit.getOfflinePlayer(giveawayEnters.get(i));
					i = r.nextInt(giveawayEnters.size());
					giveawayEnters.remove(player.getUniqueId());
				} while(!giveawayEnters.isEmpty() ||!player.isOnline());
				
				player.getPlayer().getInventory().addItem(new ItemStack(mat, r.nextInt(33)+32));
				Bukkit.broadcastMessage(MainCommand.prefix+"§6§l" + player.getName() + " HAS WON THE GIVEAWAY!");
				
				runningGiveaway = false;
				giveawayEnters = new ArrayList<>();
			}
			
		}, 150*20);
	}
	
	public static void randomItem(Player p) {
		Material mat = ItemUtils.getRandomMaterial();
		p.getInventory().addItem(new ItemStack(mat, r.nextInt(17)+48));
		p.sendMessage(MainCommand.prefix+ "§aYou were given " + mat.toString());
	}
	
	public static void randomItemAll(Player p ) {
		Material mat = ItemUtils.getRandomMaterial();
		for(Player player : Bukkit.getOnlinePlayers())
			player.getInventory().addItem(new ItemStack(mat, r.nextInt(17)+48));
		Bukkit.broadcastMessage(MainCommand.prefix+ "§aYou were given " + mat.toString() + " by the ItemAll of " + p.getName());
	}
	
	public static void dropParty(Player p) {
		Location loc = new Location(p.getWorld(), r.nextInt(4000)-2000, 100, r.nextInt(4000)-2000);
		Bukkit.broadcastMessage(MainCommand.prefix+"§6 DROPPARTY BY "+p.getName().toUpperCase()+" STARTING IN 60 SECONDS! X:" + loc.getBlockX()+" Z:" + loc.getBlockZ());
		loc.setY(loc.getWorld().getHighestBlockAt(loc).getLocation().getY()+70);
		
		BukkitRunnable run = new BukkitRunnable() {
			
			boolean started = false;
			int i = 0;

			@Override
			public void run() {
				if(!started) {
					Bukkit.broadcastMessage(MainCommand.prefix + "§6DROPPARTY BY "+p.getName().toUpperCase()+" STARTING NOW! X:" + loc.getBlockX()+" Z:" + loc.getBlockZ());
					started = true;
				}
				
				Location l = new Location(loc.getWorld(), (r.nextInt(20)-10)+loc.getBlockX(), 
						loc.getBlockY(), 
						(r.nextInt(20)-10)+loc.getBlockZ());
				l.getWorld().dropItemNaturally(l, new ItemStack(ItemUtils.getRandomMaterial(), r.nextInt(17)+48));
				if(i >= 99) {
					cancel();
					return;
				}
				i++;
				
			}
		};
		
		run.runTaskTimer(Main.getInstance(), 60*20, 20);
	}
	
	public static void airDrop(Player p) {
		Location loc = new Location(p.getWorld(), r.nextInt(4000)-2000, 100, r.nextInt(4000)-2000);
		loc.getBlock().setType(Material.CHEST);
		BlockData data = loc.getBlock().getBlockData();
		loc.getBlock().setType(Material.AIR);
		Bukkit.broadcastMessage(MainCommand.prefix+"§6Airdrop incoming by " + p.getName() + "! X:" + loc.getBlockX() + " Z:" +loc.getBlockZ());
		p.getWorld().spawnFallingBlock(loc, data);
	}
	
	@EventHandler
    public void fallingChest(EntityChangeBlockEvent event){
        if (event.getEntity() instanceof FallingBlock){
            FallingBlock fb = (FallingBlock) event.getEntity();
            if (fb.getMaterial() == Material.CHEST){
                event.setCancelled(true);
                event.getBlock().setType(Material.CHEST);
                Chest chest = (Chest) event.getBlock().getState();
                Inventory inv = chest.getInventory();
                for(int i = 0; i < inv.getSize(); i++) {
                	if(r.nextBoolean()) {
                		inv.setItem(i, new ItemStack(ItemUtils.getRandomMaterial(), r.nextInt(64)+1));
                	}
                }
                
            }
        }
    }

}
