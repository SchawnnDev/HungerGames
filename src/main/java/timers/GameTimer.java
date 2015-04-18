package timers;

import main.BGMain;

import org.bukkit.Bukkit;

import utilities.*;

public class GameTimer {
	
	private static Integer shed_id = null;

	public GameTimer() {
		shed_id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BGMain.instance, new Runnable() {
			
			public void run() {
				BGMain.GAME_RUNNING_TIME++;
				BGChat.printTimeChat("");
				BGMain.checkwinner();
				BGVanish.updateVanished();

				if ((BGMain.GAME_RUNNING_TIME % 5 != 0) && (BGMain.GAME_RUNNING_TIME % 10 != 0)) {
					if(BGMain.SHOW_TIPS) {	
						BGChat.printTipChat();
					}
				}
				
				if(BGMain.FEAST) {
					if (BGMain.GAME_RUNNING_TIME == BGMain.FEAST_SPAWN_TIME - 3)
						BGFeast.announceFeast(3);
					if (BGMain.GAME_RUNNING_TIME == BGMain.FEAST_SPAWN_TIME - 2)
						BGFeast.announceFeast(2);
					if (BGMain.GAME_RUNNING_TIME == BGMain.FEAST_SPAWN_TIME - 1)
						BGFeast.announceFeast(1);
					
					if (BGMain.GAME_RUNNING_TIME == BGMain.FEAST_SPAWN_TIME)
						BGFeast.spawnFeast();
				}

				if (BGMain.GAME_RUNNING_TIME >= BGMain.MAX_GAME_RUNNING_TIME)
					Bukkit.getServer().shutdown();
				
				}
			
		}, 0, 20*60);
	}
	
	public static void cancel() {
		if(shed_id != null) {
			Bukkit.getServer().getScheduler().cancelTask(shed_id);
			shed_id = null;
		}
	}
}