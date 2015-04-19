package utilities;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

import main.BGMain;

public class BGReward {
	public static HashMap<String, String> BOUGHT_KITS = new HashMap<>();
	public static HashMap<String, Integer> TRYS = new HashMap<>();
	
	public static void createUser(final String playerName) {
		Integer PL_ID = BGMain.getPlayerID(Bukkit.getPlayer(playerName).getUniqueId());
		if(PL_ID == null) {
			if(TRYS.containsKey(playerName)) {
				if(TRYS.get(playerName) >= 5) {
					BGMain.getLog().warning("Could not create a reward profile for '" + playerName + "'!");
					TRYS.remove(playerName);
					return;
				}
				TRYS.put(playerName, TRYS.get(playerName) + 1);
			} else {
				TRYS.put(playerName, 1);
			}
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BGMain.instance, new Runnable() {
				public void run() {
					createUser(playerName);
				}
			}, 20*1);
			return;
		}
		if (BGMain.getCoins(Bukkit.getPlayer(playerName).getUniqueId()) != -1) {
			BGMain.SQLquery("INSERT INTO REWARD (REF_PLAYER, COINS) VALUES ("+ Bukkit.getPlayer(playerName).getUniqueId().toString() + ", 0)");
			if(TRYS.containsKey(playerName)) 
				TRYS.remove(playerName);
		}
	}
	
	public static void giveCoins(String playerName, int coins) {
		
		BGMain.SQLquery("UPDATE REWARD SET COINS = (COINS+"+coins+") WHERE REF_PLAYER=" +Bukkit.getPlayer(playerName).getUniqueId().toString());
	}
	
	public static void takeCoins(String playerName, int coins) {
		
		BGMain.SQLquery("UPDATE REWARD SET COINS = (COINS-"+coins+") WHERE REF_PLAYER=" + Bukkit.getPlayer(playerName).getUniqueId().toString());
	}
	
	public static void coinUse(String playerName, String kitName) {
		
		BOUGHT_KITS.put(playerName, kitName);
	}
	
	public static boolean sendCoins(String sender, String dest, int coins) {
		
		int scoins = (int) BGMain.getCoins(Bukkit.getPlayer(sender).getUniqueId());
		if(scoins >= coins) {
			takeCoins(sender, coins);
			giveCoins(dest, coins);
			return true;
		}
		return false;
	}
}
