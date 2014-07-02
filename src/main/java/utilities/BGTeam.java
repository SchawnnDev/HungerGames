package utilities;

import java.util.ArrayList;
import java.util.HashMap;


import org.bukkit.entity.Player;

public class BGTeam {
	static HashMap<Player, ArrayList<String>> teams = new HashMap<Player, ArrayList<String>>();
	
	public static void addMember(Player player, String memberName) {
		
		ArrayList<String> members = teams.get(player);
		if(members == null)
			members = new ArrayList<String>();
		
		members.add(memberName);
		teams.put(player, members);
	}
	
	public static void removeMember(Player player, String memberName) {
		
		ArrayList<String> members = teams.get(player);
		if(members == null)
			return;
		
		members.remove(memberName);
		teams.put(player, members);
	}
	
	public static ArrayList<String> getTeamList(Player player) {
		
		ArrayList<String> members = teams.get(player);
		
		if(members == null)
			return null;
		if(members.size() == 0)
			return null;
		
		return members;
	}
	
	public static boolean isInTeam(Player player, String memberName) {
		
		ArrayList<String> members = teams.get(player);
		
		
		try{
			if(members.contains(memberName))
				return true;
		}catch(NullPointerException e){
			return false;
		}
		
		return false;
	}
}
