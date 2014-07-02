package timers;

import main.BGMain;

import org.bukkit.Bukkit;

import utilities.BGChat;
import utilities.BGFeast;
import utilities.BGVanish;
import utilities.Border;
import utilities.enums.BorderType;

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

				if(BGMain.GAME_ENDING_TIME <= BGMain.GAME_RUNNING_TIME) {
					if(!BGMain.BORDERS.containsKey(BorderType.SHRINK)) {
						if(BGMain.WORLDRADIUS > 40) {
							Integer shrink_size = 20;
							if(BGMain.WORLDRADIUS > 300)
								shrink_size = 60;
							else if(BGMain.WORLDRADIUS > 200)
								shrink_size = 40;
							else if(BGMain.WORLDRADIUS > 100)
								shrink_size = 20;
							else if(BGMain.WORLDRADIUS < 70)
								shrink_size = 10;
							else
								shrink_size = BGMain.WORLDRADIUS - 40;
							
							BGMain.WORLDRADIUS = BGMain.WORLDRADIUS - shrink_size;
							BGMain.BORDERS.put(BorderType.SHRINK, new Border(BGMain.spawn.getX(), BGMain.spawn.getZ(), BGMain.WORLDRADIUS - 5));
							BGChat.printInfoChat("World-border will shrink " + shrink_size + " blocks in one minute!");
						}
					} else {
						BGMain.BORDERS.remove(BorderType.STOP);
						BGMain.BORDERS.remove(BorderType.WARN);
						BGMain.BORDERS.put(BorderType.STOP, BGMain.BORDERS.get(BorderType.SHRINK));
						BGMain.BORDERS.put(BorderType.WARN, new Border(BGMain.spawn.getX(), BGMain.spawn.getZ(), BGMain.WORLDRADIUS - 10));
						BGMain.BORDERS.remove(BorderType.SHRINK);
						BGChat.printInfoChat("World-border shrinked!");
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