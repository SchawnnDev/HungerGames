package timers;

import java.util.HashMap;
import java.util.Random;

import main.BGMain;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import utilities.BGChat;
import utilities.enums.BorderType;
import utilities.enums.GameState;

public class WorldBorderTimer {

	private static Integer shed_id = null;
	private static HashMap<Player, Location> locations = new HashMap<Player, Location>();

	public WorldBorderTimer() {
		shed_id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BGMain.instance, new Runnable() {
			
			public void run() {				
				Random r = new Random();
				for(Player p : BGMain.getPlayers()) {						
					if (!BGMain.inBorder(p.getLocation(), BorderType.STOP)) {
						p.playSound(p.getLocation(), Sound.FIZZ, 1.0F, (byte) 1);
						BGChat.printPlayerChat(p, ChatColor.RED + "" +  ChatColor.BOLD + BGMain.instance.getConfig().getString("MESSAGE.WORLD_BORDER"));
						
						if(BGMain.isGameMaker(p) || BGMain.isSpectator(p) || BGMain.GAMESTATE != GameState.GAME) {
							if(p.isInsideVehicle())
								p.getVehicle().eject();
							
							if(locations.containsKey(p) && BGMain.inBorder(locations.get(p), BorderType.STOP))
								p.teleport(locations.get(p));
							else
								p.teleport(BGMain.getSpawn());
							
							locations.put(p, p.getLocation());
							continue;
						}
						
						p.damage(r.nextBoolean() ? 4 : 3);
						continue;
					}
					
					if(BGMain.GAMESTATE != GameState.PREGAME && !BGMain.inBorder(p.getLocation(), BorderType.SHRINK)) {
						p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, (byte) 1);
						BGChat.printPlayerChat(p, ChatColor.RED + "" + ChatColor.ITALIC + "You will be outside the shrinked world-border!");
						continue;
					}
					
					if(BGMain.GAMESTATE != GameState.PREGAME && !BGMain.inBorder(p.getLocation(), BorderType.WARN)) {
						p.playSound(p.getLocation(), Sound.NOTE_PLING, 1.0F, (byte) 1);
						BGChat.printPlayerChat(p, ChatColor.RED + "" + ChatColor.ITALIC + "You are coming close to the world-border!");
					}
					
					if(BGMain.isGameMaker(p) || BGMain.isSpectator(p) || BGMain.GAMESTATE != GameState.GAME)
						locations.put(p, p.getLocation());				
				}
			}
			
		}, 0, 20*2);
	}
	
	public static void cancel() {
		if(shed_id != null) {
			Bukkit.getServer().getScheduler().cancelTask(shed_id);
			shed_id = null;
		}
	}
	
}
