package commands;

import java.util.logging.Logger;

import main.BGMain;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import utilities.BGChat;
import utilities.BGKit;
import utilities.BGReward;
import utilities.enums.GameState;
import utilities.enums.Translation;

public class BGConsole implements CommandExecutor {
	Logger log = BGMain.getLog();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player p;
		if(sender instanceof Player) {
			p = (Player) sender;
		}else {
			p = null;
		}
		if (cmd.getName().equalsIgnoreCase("start")) {
			if (sender.hasPermission("bg.admin.start") || sender.hasPermission("bg.admin.*")) {
				if (BGMain.GAMESTATE != GameState.PREGAME)			
					msg(p, sender, ChatColor.RED + Translation.GAME_BEGUN.t());
				else
					BGMain.startgame();
			} else {
				msg(p, sender, ChatColor.RED + Translation.NO_PERMISSION.t());
			}
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("coin")) {
			
			if (!BGMain.REW) {
				msg(p, sender, ChatColor.RED + Translation.REWARD_FUNC_DISABLED.t());
				return true;
			}
			
			int coins = 0;
			
			if(p == null && ((args.length < 1) || (args.length > 0 && (!args[0].equalsIgnoreCase("give") && !args[0].equalsIgnoreCase("take"))))) {
				sender.sendMessage(ChatColor.RED + Translation.CMD_ONLY_PLAYER_ACCESS.t());
				return true;
			} else if(p != null) {
				coins = BGMain.getCoins(p.getUniqueId());
			}
			
			if (args.length == 0) {
				BGChat.printPlayerChat(p, ChatColor.YELLOW + Translation.REWARD_FUNC_CMDS.t());
				return true;
			} else if (args.length > 3) {
				return false;
			} else if (args[0].equalsIgnoreCase("buy")) {
				if(BGMain.isSpectator(p) || BGMain.isGameMaker(p)) {
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.REWARD_FUNC_NOT_ALLOWED.t());
					return true;
				}
				
				if(BGMain.GAMESTATE != GameState.PREGAME) {
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.REWARD_FUNC_NOT_ALLOWED.t());
					return true;
				}
					
				if(args.length < 2) {
					return false;
				}
				
				String kitName = args[1];
				if (p.hasPermission("bg.kit.*")) {
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.REWARD_FUNC_ALL_KITS.t());
				}else if (BGMain.REW && BGReward.BOUGHT_KITS.containsKey(p.getName())) {
					
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.REWARD_FUNC_ALREADY_BOUGHT.t());
					return true;					
				} else if (BGKit.isKit(kitName.toLowerCase()) == false) {
					
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.KIT_NOT_EXIST.t());
					return true;
				} else if(BGKit.getCoins(kitName.toLowerCase()) == 0) {
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.REWARD_FUNC_KIT_NO_BUY.t());
					return true;
				} else if(coins >= BGKit.getCoins(kitName.toLowerCase())) {
					BGReward.coinUse(p.getName(), kitName.toLowerCase());
					BGReward.takeCoins(p.getName(), BGKit.getCoins(kitName.toLowerCase()));
					BGKit.setKit(p, kitName.toLowerCase());
					BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.REWARD_FUNC_BOUGHT_KIT.t().replace("<kit>", kitName));
					return true;
				} else {
					
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.REWARD_FUNC_NOT_ENOUGH_COINS.t());
					return true;
				}
			}else if (args[0].equalsIgnoreCase("send")) {
				
				if (args.length < 3) {
					return false;
				}
				if (BGMain.getPlayerID(Bukkit.getPlayer(args[1]).getUniqueId()) == 0) {
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.PLAYER_NOT_ONLINE.t());
					return true;
				}else {
					
					int zahl;
					try{
						zahl = Integer.parseInt(args[2]);
					}catch (Exception e) {
						BGChat.printPlayerChat(p, ChatColor.RED + Translation.REWARD_FUNC_INVALID_INT.t());
						return true;
					}
					
					if(zahl < 0) {
						BGChat.printPlayerChat(p, ChatColor.RED + Translation.REWARD_FUNC_INVALID_INT.t());
						return true;
					}
						
					if (zahl > coins) {
						BGChat.printPlayerChat(p, ChatColor.RED + Translation.REWARD_FUNC_NOT_ENOUGH_COINS.t());
						return true;
					} else {
						BGReward.sendCoins(p.getName(), args[1], zahl);
							if (Bukkit.getServer().getPlayer(args[1]) == null) {	
								BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.REWARD_FUNC_SUCCESS_COIN_SENT.t());
								return true;
							} else {
								BGChat.printPlayerChat(p, ChatColor.GREEN +Translation.REWARD_FUNC_SUCCESS_COIN_SENT.t());
								BGChat.printPlayerChat(Bukkit.getServer().getPlayer(args[1]), ChatColor.GREEN + Translation.REWARD_FUNC_SUCCESS_COIN_RECEIVED.t());
								return true;
							}
						
						}
					}
				
			} else if(args[0].equalsIgnoreCase("give")) {
					if (sender.hasPermission("bg.admin.transaction")) {	
						if (args.length < 3) {
							return false;
						}
						if (BGMain.getPlayerID(Bukkit.getPlayer(args[1]).getUniqueId()) == 0) {
							msg(p, sender, ChatColor.RED + Translation.PLAYER_NOT_ONLINE.t());
							return true;
						}
						
						int zahl;
						try {	
							zahl = Integer.parseInt(args[2]);
						}catch (Exception e) {
							msg(p, sender, ChatColor.RED + Translation.REWARD_FUNC_INVALID_INT.t());
							return true;
						}
							BGReward.giveCoins(args[1], zahl);
							msg(p, sender, ChatColor.RED + Translation.REWARD_FUNC_SUCCESS_TRANSACTION.t());
							if(Bukkit.getServer().getPlayer(args[2]) == null)
								return true;
							
							BGChat.printPlayerChat(Bukkit.getServer().getPlayer(args[1]), ChatColor.GREEN + Translation.REWARD_FUNC_SUCCESS_COIN_RECEIVED.t());
							return true;
						
					} else {
						BGChat.printPlayerChat(p, ChatColor.RED + Translation.NO_PERMISSION.t());
						return true;
					}
			} else if(args[0].equalsIgnoreCase("stats")) {
					if(sender.hasPermission("bg.admin.stats")) {
						if (args.length < 2) {
							return false;
						}
						
						if (BGMain.getPlayerID(Bukkit.getPlayer(args[1]).getUniqueId()) == 0) {
							msg(p, sender, ChatColor.RED + Translation.PLAYER_NOT_ONLINE.t());
							return true;
						}
						
						int coins1 = BGMain.getCoins(p.getUniqueId());
						msg(p, sender, ChatColor.YELLOW + Translation.REWARD_FUNC_STATS.t().replace("<player>", args[1]).replace("<coins>", coins1 + ""));
					} else {
						BGChat.printPlayerChat(p, ChatColor.RED + Translation.NO_PERMISSION.t());
						return true;
					}
				}
			}
			return true;
	}
	
	private static void msg(Player p, CommandSender s, String msg) {
		if(p == null)
			s.sendMessage(msg);
		else
			BGChat.printPlayerChat(p, msg);
	}
}