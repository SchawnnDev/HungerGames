package utilities;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import main.BGMain;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import events.BGAbilitiesListener;

public class BGCooldown {

	public BGCooldown() {
		BGFiles.abconf = YamlConfiguration.loadConfiguration(
				new File(BGMain.instance.getDataFolder(), "abilities.yml"));
	}
	
	public static void monkCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.monkList.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.13.Cooldown") * 1000);
	}
	
	public static void thiefCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.thiefList.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.15.Cooldown") * 1000);
	}
	
	public static void ghostCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.ghostList.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.16.Cooldown") * 1000);
	}
	
	public static void viperCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.viperList.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.19.Duration") * 1000);
	}
	
	public static void thorCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run(){
				
				BGAbilitiesListener.thorList.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.11.Cooldown") * 1000);
	}
	
	public static void timeCooldown(final Player player) {
		
		TimerTask action = new TimerTask() {
			
			public void run() {
				
				BGAbilitiesListener.timeList.remove(player);
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.22.Cooldown")*1000);
	}
	
	public static void freezeCooldown(final Player player) {
		
		TimerTask action = new TimerTask(){
			
			public void run() {
				
				BGAbilitiesListener.freezeList.remove(player);
				BGChat.printPlayerChat(player, BGFiles.abconf.getString("AB.22.unfrozen"));
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(action, BGFiles.abconf.getInt("AB.22.Duration")*1000);
	}
}
