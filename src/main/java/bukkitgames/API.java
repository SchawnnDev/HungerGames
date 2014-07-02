package bukkitgames;

import java.util.ArrayList;

import main.BGMain;

import org.bukkit.entity.Player;

import utilities.BGKit;
import utilities.enums.GameState;

public class API {

	/**
	 * Adds a new ability
	 * 
	 * @param id
	 * 			Ability ID
	 * @param desc
	 * 			Ability Description
	 */
	public static void addAbility(Integer id, String desc) throws Error {
		if(BGKit.getAbilityDesc(id) != null)
			throw new Error("Ability ID " + id + " is already registered.");
		
		BGKit.setAbilityDesc(id, desc);
	}
	
	
	/**
	 * Returns true if the given player has the ability
	 * 
	 * @param player
	 * 			The player to be checked
	 * @param id
	 * 			Ability ID
	 * @return Boolean
	 */
	public static Boolean playerHasAbility(Player player, Integer id) {
		return BGKit.hasAbility(player, id);
	}
	
	/**
	 * Returns the name of the kit the player has selected
	 * 
	 * @param player
	 * 			The player to get the kit from
	 * @return String
	 */
	public static String getPlayerKit(Player player) {
		return BGKit.getKit(player);
	}
	
	/**
	 * Returns true if the given kit exists
	 * 
	 * @param kit
	 * 			The kit to check
	 * @return Boolean
	 */
	public static Boolean getKitExists(String kit) {
		return BGKit.isKit(kit);
	}
	
	/**
	 * Returns all available kits
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<String> getKits() {
		return BGKit.kits;
	}
	
	/**
	 * Returns the current GameState
	 * 
	 * @return GameState
	 */
	public static GameState getGameState() {
		return BGMain.GAMESTATE;
	}
	
	/**
	 * Returns online GameMakers
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Player> getOnlineGamemakers() {
		return BGMain.getGamemakers();
	}
	
	/**
	 * Returns online spectators
	 * 
	 * @return ArrayList
	 */
	public static ArrayList<Player> getOnlineSpectators() {
		return BGMain.getSpectators();
	}
	
	/**
	 * Returns all online gamers
	 * 
	 * @return Player[]
	 */
	public static Player[] getOnlineGamers() {
		return BGMain.getGamers();
	}
}
