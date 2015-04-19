package timers;

import main.BGMain;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import utilities.BGChat;
import utilities.enums.GameState;

public class InvincibilityTimer {

private static Integer shed_id = null;
	
	public InvincibilityTimer() {
		shed_id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BGMain.instance, new Runnable() {
			
			public void run() {
				if (BGMain.FINAL_COUNTDOWN > 0) {
					if (BGMain.FINAL_COUNTDOWN >= 10
							& BGMain.FINAL_COUNTDOWN % 10 == 0) {
						BGChat.printTimeChat("Invincibility wears off in "
								+ BGMain.TIME(BGMain.FINAL_COUNTDOWN)
								+ ".");
					} else if (BGMain.FINAL_COUNTDOWN < 10) {
						BGChat.printTimeChat("Invincibility wears off in "
								+ BGMain.TIME(BGMain.FINAL_COUNTDOWN)
								+ ".");
						for (Player pl : BGMain.getGamers()) {
							pl.playSound(pl.getLocation(), Sound.CLICK, 1.0F, (byte) 1);
						}
					}
					BGMain.FINAL_COUNTDOWN--;
				} else {
					BGChat.printTimeChat("");
					BGChat.printTimeChat("Invincibility was worn off.");
					BGMain.getLog().info("Game phase: 3 - Fighting");
					for (Player pl : BGMain.getGamers()) {
						pl.playSound(pl.getLocation(), Sound.ANVIL_LAND, 1.0F, (byte) 1);
					}
					if(BGMain.SHOW_TIPS) {
						BGChat.printTipChat();
					}
					BGMain.GAMESTATE = GameState.GAME;
					new GameTimer();
					cancel();
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