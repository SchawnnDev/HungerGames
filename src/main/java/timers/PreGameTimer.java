package timers;

import main.BGMain;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import utilities.BGChat;

public class PreGameTimer {

	private static Integer shed_id = null;
	
	public PreGameTimer() {
		shed_id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BGMain.instance, new Runnable() {
			
			public void run() {
				if (BGMain.COUNTDOWN > 0) {
					if (BGMain.COUNTDOWN >= 10 & BGMain.COUNTDOWN % 10 == 0) {
						BGChat.printTimeChat("The game will start in "
								+ BGMain.TIME(BGMain.COUNTDOWN) + ".");
						for (Player pl : BGMain.getGamers()) {
							pl.setHealth(20);
							pl.setFoodLevel(20);
							pl.setExp(0);
							pl.setRemainingAir(20);
						}
					} else if (BGMain.COUNTDOWN < 10) {
						BGChat.printTimeChat("The game will start in "
								+ BGMain.TIME(BGMain.COUNTDOWN) + ".");
						for (Player pl : BGMain.getGamers()) {
							pl.playSound(pl.getLocation(), Sound.CLICK, 1.0F, (byte) 1);
						}
					}

					BGMain.COUNTDOWN--;
				} else if (BGMain.getGamers().length < BGMain.MINIMUM_PLAYERS) {
					BGChat.printTimeChat("There are too few players on, restarting countdown.");
					BGMain.COUNTDOWN = BGMain.COUNTDOWN_SECONDS;
				} else {
					BGMain.startgame();
				}
			}
		}, 0, 20);
	}
	
	public static void cancel() {
		if(shed_id != null) {
			Bukkit.getServer().getScheduler().cancelTask(shed_id);
			shed_id = null;
		}
	}
	
}
