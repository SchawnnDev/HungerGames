package commands;

import java.util.logging.Logger;

import main.BGMain;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import utilities.BGChat;
import utilities.BGKit;
import utilities.BGTeam;
import utilities.enums.BorderType;
import utilities.enums.GameState;
import utilities.enums.Translation;

public class BGPlayer implements CommandExecutor{

	Logger log = BGMain.getPluginLogger();
		
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			log.info(ChatColor.RED + Translation.CMD_ONLY_PLAYER_ACCESS.t());
			return true;
		}
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("help")) {
			BGChat.printHelpChat(p);
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("kitinfo")) {
			if (args.length != 1) {
				return false;
			}
			BGChat.printKitInfo(p, args[0]);
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("kit")) {
			if (BGMain.GAMESTATE != GameState.PREGAME) {
				BGChat.printPlayerChat(p, ChatColor.RED + Translation.GAME_BEGUN.t());
				return true;
			}
			if (args.length != 1) {
				BGChat.printKitChat(p);
				return true;
			}
			BGKit.setKit(p, args[0]);
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("spawn")) {
			if (BGMain.GAMESTATE != GameState.PREGAME && !p.hasPermission("bg.admin.spawn") && !(BGMain.isGameMaker(p) || BGMain.isSpectator(p))) {
				BGChat.printPlayerChat(p, Translation.GAME_BEGUN.t());
				return true;
			} else {
				p.teleport(BGMain.getSpawn());
				BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.TELEPORTED_SPAWN.t());
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("team")) {
			if(!BGMain.TEAM) {
				BGChat.printPlayerChat(p, ChatColor.RED + Translation.TEAM_FUNC_DISABLED.t());
				return true;
			}
			
			if(!p.hasPermission("bg.team")) {
				BGChat.printPlayerChat(p, ChatColor.RED + Translation.NO_PERMISSION.t());
				return true;
			}
			
			if(args.length > 2) {
				return false;
			}
			
			if (args.length == 0) {
				BGChat.printPlayerChat(p, ChatColor.YELLOW + Translation.TEAM_FUNC_CMDS.t());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("add")) {
				if(args.length < 2) {
					return false;
				}
				
				if(Bukkit.getServer().getPlayer(args[1]) == null) {
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.PLAYER_NOT_ONLINE.t());
					return true;
				}
				
				Player player = Bukkit.getServer().getPlayer(args[1]);
				
				if(BGTeam.isInTeam(p, player.getName())){
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.TEAM_FUNC_PLAYER_ALREADY_TEAM.t());
					return true;
				}
					
				BGTeam.addMember(p, player.getName());
				BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.TEAM_FUNC_ADDED_PLAYER.t());
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("remove")) {
				if(args.length < 2) {
					return false;
				}
				
				if(!BGTeam.isInTeam(p, args[1])) {
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.TEAM_FUNC_PLAYER_ALREADY_TEAM.t());
					return true;
				}
				
				BGTeam.removeMember(p, args[1]);
				BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.TEAM_FUNC_REMOVED_PLAYER.t());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("list")) {
				if(args.length < 1 || args.length > 1) {
					return false;
				}
								
				String text = ChatColor.YELLOW + Translation.TEAM_FUNC_YOUR_TEAM.t();
				
				if(BGTeam.getTeamList(p).size() == 0) {
					text += " Nobody";
					BGChat.printPlayerChat(p, text);
					return true;
				}
				
				for(String t : BGTeam.getTeamList(p)) {
					text += " " + t;
				}
				BGChat.printPlayerChat(p, text);
				return true;
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("teleport")) {
			if(BGMain.isGameMaker(p) || BGMain.isSpectator(p)) {
				if(args.length > 2) {
					return false;
				}
				
				if(args.length == 0) {
					BGChat.printPlayerChat(p, ChatColor.YELLOW + Translation.TELEPORT_FUNC_CMDS.t());
					return true;
				}
				
				if(args.length == 1) {
					
					if(Bukkit.getServer().getPlayer(args[0]) == null) {
						BGChat.printPlayerChat(p, ChatColor.RED + Translation.PLAYER_NOT_ONLINE.t());
						return true;
					}
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.TELEPORT_FUNC_TELEPORTED_PLAYER.t().replace("<player>", target.getName()));
					p.teleport(target);
					return true;
				}
				
				if(args.length == 2) {
					int x = 0;
					int z = 0;
					
					try {
						x = Integer.parseInt(args[0]);
						z = Integer.parseInt(args[1]);
					} catch(NumberFormatException e) {
						BGChat.printPlayerChat(p, ChatColor.RED + Translation.TELEPORT_FUNC_COORDS_NOT_VALID.t());
						return true;
					}
					
					Location loc = new Location(Bukkit.getServer().getWorlds().get(0), x, Bukkit.getServer().getWorlds().get(0).getHighestBlockYAt(x, z)+1.5, z);
					if(!BGMain.inBorder(loc, BorderType.WARN)) {
						BGChat.printPlayerChat(p, ChatColor.RED + Translation.TELEPORT_FUNC_COORDS_NOT_VALID.t());
						return true;
					}
					BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.TELEPORT_FUNC_TELEPORTED_COORDS.t().replace("<x>", x + "").replace("<z>", z + ""));
					p.teleport(loc);
					return true;
				}
			} else {
				BGChat.printPlayerChat(p, ChatColor.RED + Translation.NO_PERMISSION.t());
				return true;
			}
		}
		
		return true;
	}
}
