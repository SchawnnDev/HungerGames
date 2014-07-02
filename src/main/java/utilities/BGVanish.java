package utilities;

import java.util.ArrayList;
import java.util.logging.Logger;

import main.BGMain;

import org.bukkit.entity.Player;

public class BGVanish {
	Logger log = BGMain.getPluginLogger();
	static ArrayList<String> vanished = new ArrayList<String>();

	public static void makeVanished(Player p) {
		for (Player player : BGMain.getPlayers()) {
			if (player.getName().equals(p.getName()))
				continue;
			
			if(player.isOp() && BGMain.PLAYERS_VISIBLE)
				continue;

			player.hidePlayer(p);
		}
		vanished.add(p.getName());
	}

	public static boolean isVanished(Player p) {
		return vanished.contains(p.getName());
	}

	public static void updateVanished() {
		for (Player p : BGMain.getPlayers())
			if (isVanished(p)) {
				makeVanished(p);
			} else {
				makeVisible(p);
			}
	}

	public static void makeVisible(Player p) {
		for (Player player : BGMain.getPlayers()) {
			player.showPlayer(p);
		}
		vanished.remove(p.getName());
	}
}