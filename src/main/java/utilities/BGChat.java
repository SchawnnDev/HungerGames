package utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import main.BGMain;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;


public class BGChat {
	private static Logger log = BGMain.getPluginLogger();
	static Integer TIP_COUNT = 0;
	static ArrayList<String> TIPS = new ArrayList<String>();
	static HashMap<Player, IconMenu> MENUS = new HashMap<Player, IconMenu>();
	
	static Boolean update = true;

	public BGChat() {		
		List<String> tiplist = BGFiles.config.getStringList("TIPS");
		for(String tip : tiplist)
			TIPS.add(tip);
	}

	public static void printInfoChat(String text) {
		Bukkit.getServer().broadcastMessage(ChatColor.DARK_GREEN + text);
	}

	public static void printDeathChat(String text) {
		Bukkit.getServer().broadcastMessage(ChatColor.RED + text);
	}

	public static void printTimeChat(String text) {
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + text);
	}

	public static void printPlayerChat(Player player, String text) {
			player.sendMessage(ChatColor.GRAY + text);
	}

	public static void printHelpChat(Player player) {
			BGChat.printPlayerChat(player, BGMain.SERVER_TITLE);
			String are = "are";
			String players = "players";
			if (BGMain.getGamers().length == 1) {
				are = "is";
				players = "player";
			}

			Integer timeleft = BGMain.MAX_GAME_RUNNING_TIME
					- BGMain.GAME_RUNNING_TIME;
			String is = "are";
			String minute = "minutes";
			if (timeleft <= 1) {
				is = "minute";
				minute = "minute";
			}
			player.sendMessage(ChatColor.GRAY + " - There " + are + " "
					+ BGMain.getGamers().length + " " + players + " online.");
			player.sendMessage(ChatColor.GRAY + " - There " + is + " " + timeleft + " "
					+ minute + " left.");
			if (BGMain.HELP_MESSAGE != null && BGMain.HELP_MESSAGE != "")
				player.sendMessage(ChatColor.GRAY + " - " + BGMain.HELP_MESSAGE);
	}

	public static void printKitChat(Player player) {
		if(!BGMain.ITEM_MENU) {
			
			Set<String> kitname = BGFiles.kitconf.getKeys(false);
			String yourkits = "";
			String otherkits = "";
			for (String name : kitname) {
				if(name.equalsIgnoreCase("default"))
					continue;
				
				char[] stringArray = name.toCharArray();
				stringArray[0] = Character.toUpperCase(stringArray[0]);
				name = new String(stringArray);
				
				if (player.hasPermission("bg.kit." + name.toLowerCase())
						|| player.hasPermission("bg.kit.*") || (BGMain.SIMP_REW && BGMain.winner(player))
						|| (BGMain.REW && BGReward.BOUGHT_KITS.get(player.getName()) != null &&
							BGReward.BOUGHT_KITS.get(player.getName()).equals(name.toLowerCase()))) {
					yourkits = name + ", " + yourkits;
				} else {
					otherkits = name + ", " + otherkits;
				}
			}
			player.sendMessage("");
			player.sendMessage(ChatColor.AQUA + "Available kits: " + ChatColor.GRAY + "(/kit [KitName])");
			player.sendMessage("");
			player.sendMessage(ChatColor.GREEN + "Your kits: " + ChatColor.WHITE + yourkits);
			player.sendMessage(ChatColor.GREEN + "Other kits: " + ChatColor.WHITE + otherkits);
			player.sendMessage(ChatColor.AQUA + "More kits available at: "
					+ BGMain.KIT_BUY_WEB);
			player.sendMessage("");
		
		} else {
			 Set<String> kits = BGFiles.kitconf.getKeys(false);
			 
			 Integer invsize = 9;
			 for(int i=0; i<=10; i++) {
				 if((i*9) >= kits.size()) {
					 invsize = invsize + i*9;
					 break;
				 }
			 }
			 
			 if(MENUS.containsKey(player)) {
				 MENUS.get(player).destroy();
				 MENUS.remove(player);
			 }
			 
			 final Player pl = player;
			 IconMenu menu = new IconMenu("Select a kit", pl.getName(), invsize, new IconMenu.OptionClickEventHandler() {
		            public void onOptionClick(IconMenu.OptionClickEvent event) {
		            	BGKit.setKit(pl, ChatColor.stripColor(event.getName()));
		                event.setWillClose(true);
		                event.setWillDestroy(false);
		            }
		        }, BGMain.instance);
			 
			 Integer mypos = 0;
			 Integer othpos = 1;
			 for(String kitname : kits) {	
				 try {
					 if(kitname.equalsIgnoreCase("default"))
						 continue;
					 
						char[] stringArray = kitname.toCharArray();
						stringArray[0] = Character.toUpperCase(stringArray[0]);
						kitname = new String(stringArray);
					 
						ArrayList<String> container = new ArrayList<String>();
						ConfigurationSection kit = BGFiles.kitconf.getConfigurationSection(kitname.toLowerCase());
						List<String> kititems = kit.getStringList("ITEMS");
						for (String item : kititems) {
							String[] oneitem = item.split(",");

							String itemstring = null;
							Integer id = null;
							Integer amount = null;
							String enchantment = null;
							String ench_numb = null;

							if (oneitem[0].contains(":")) {
								String[] ITEM_ID = oneitem[0].split(":");
								id = Integer.valueOf(Integer.parseInt(ITEM_ID[0]));
								amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
							} else {
								id = Integer.valueOf(Integer.parseInt(oneitem[0]));
								amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
							}

							itemstring = " - "
									+ amount
									+ "x "
									+ Material.getMaterial(id.intValue()).toString()
											.replace("_", " ").toLowerCase();

							if (oneitem.length == 4) {
								enchantment = Enchantment
										.getById(Integer.parseInt(oneitem[2])).getName()
										.toLowerCase();
								ench_numb = oneitem[3];

								itemstring = itemstring + " with " + enchantment + " "
										+ ench_numb;
							}

							container.add(ChatColor.WHITE + itemstring);
						}
						
						List<String> pots = kit.getStringList("POTION");
						for(String pot : pots) {	
							if (pot != null & pot != "") {
								if (!pot.equals(0)) {
									String[] potion = pot.split(",");
									if (Integer.parseInt(potion[0]) != 0) {
										PotionEffectType pt = PotionEffectType.getById(Integer.parseInt(potion[0]));
										String name = pt.getName();
										if (Integer.parseInt(potion[1]) == 0) {
											name += " (Duration: infinitely)";
										} else {
											name += " (Duration: "+potion[1]+" sec)";
										}
										container.add(ChatColor.WHITE + " * " + name);
									}
								}
							}
						}
						
						List<Integer> abils = kit.getIntegerList("ABILITY");
						for(Integer abil : abils) {
							String desc = BGKit.getAbilityDesc(abil.intValue());
							if (desc != null)
								container.add(ChatColor.WHITE + " + " + desc);
						}
									
						Integer itemid = kit.getInt("ITEMMENU");
						Material kitem = Material.getMaterial(itemid);
					    
						if (player.hasPermission("bg.kit." + kitname.toLowerCase())
								|| player.hasPermission("bg.kit.*") || (BGMain.SIMP_REW && BGMain.winner(player))
								|| (BGMain.REW && BGReward.BOUGHT_KITS.get(player.getName()) != null &&
									BGReward.BOUGHT_KITS.get(player.getName()).equals(kitname.toLowerCase()))) {
					    
							String[] info = new String[container.size()];
						    info = container.toArray(info);
							
							menu.setOption(mypos, new ItemStack(kitem, 1), ChatColor.GREEN + kitname, info);
							mypos++;
						} else {
							if(BGMain.REW) {
								if(BGKit.getCoins(kitname.toLowerCase()) == 1)
									container.add(ChatColor.GOLD + "PRICE: " + BGKit.getCoins(kitname.toLowerCase()) + " Coin");
								else if(BGKit.getCoins(kitname.toLowerCase()) > 1)
									container.add(ChatColor.GOLD + "PRICE: " + BGKit.getCoins(kitname.toLowerCase()) + " Coins");
							}
							
							String[] info = new String[container.size()];
						    info = container.toArray(info);
							
							menu.setOption(invsize - othpos, new ItemStack(kitem, 1), ChatColor.RED + kitname, info);
							othpos++;
						}
					container.clear();
				} catch (Exception e) {
					log.warning("[BukkitGames] Error while trying to parse kit '" + kitname + "'");
					e.printStackTrace();
				}	
			 }
			 MENUS.put(pl, menu);
			 menu.open(player);
		}
	}

	public static void printKitInfo(Player player, String kitname) {
		String kitinfoname = kitname;
		kitname = kitname.toLowerCase();
		ConfigurationSection kit = BGFiles.kitconf.getConfigurationSection(kitname);
		if (kit == null || !BGKit.isKit(kitname)) {
			printPlayerChat(player,
					"That kit doesn't exist! View all kits with /kit");
			return;
		}
			char[] stringArray = kitinfoname.toCharArray();
			stringArray[0] = Character.toUpperCase(stringArray[0]);
			kitinfoname = new String(stringArray);

			player.sendMessage(ChatColor.GREEN  + kitinfoname + " kit includes:");

			List<String> kititems = kit.getStringList("ITEMS");
			for (String item : kititems) {
				String[] oneitem = item.split(",");

				String itemstring = null;
				Integer id = null;
				Integer amount = null;
				String enchantment = null;
				String ench_numb = null;

				if (oneitem[0].contains(":")) {
					String[] ITEM_ID = oneitem[0].split(":");
					id = Integer.valueOf(Integer.parseInt(ITEM_ID[0]));
					amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
				} else {
					id = Integer.valueOf(Integer.parseInt(oneitem[0]));
					amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
				}

				itemstring = " - "
						+ amount
						+ "x "
						+ Material.getMaterial(id.intValue()).toString()
								.replace("_", " ").toLowerCase();

				if (oneitem.length == 4) {
					enchantment = Enchantment
							.getById(Integer.parseInt(oneitem[2])).getName()
							.toLowerCase();
					ench_numb = oneitem[3];

					itemstring = itemstring + " with " + enchantment + " "
							+ ench_numb;
				}

				player.sendMessage(ChatColor.WHITE + itemstring);
			}

			List<String> pots = kit.getStringList("POTION");
			for(String pot : pots) {	
				if (pot != null & pot != "") {
					if (!pot.equals(0)) {
						String[] potion = pot.split(",");
						if (Integer.parseInt(potion[0]) != 0) {
							PotionEffectType pt = PotionEffectType.getById(Integer.parseInt(potion[0]));
							String name = pt.getName();
							if (Integer.parseInt(potion[1]) == 0) {
								name += " (Duration: infinitely)";
							} else {
								name += " (Duration: "+potion[1]+" sec)";
							}
							player.sendMessage(ChatColor.WHITE + " * " + name);
						}
					}
				}
			}
			
			List<Integer> abils = kit.getIntegerList("ABILITY");
			for(Integer abil : abils) {
				String desc = BGKit.getAbilityDesc(abil.intValue());

				if (desc != null) {
					player.sendMessage(ChatColor.WHITE + " + " + desc);

				}
			}
				
			if(BGKit.getCoins(kitname.toLowerCase()) == 1)
				player.sendMessage(ChatColor.WHITE + "PRICE: "+ BGKit.getCoins(kitname.toLowerCase())+ " Coin");
			else if(BGKit.getCoins(kitname.toLowerCase()) > 1)
				player.sendMessage(ChatColor.WHITE + "PRICE: "+ BGKit.getCoins(kitname.toLowerCase())+ " Coins");	
	}

	public static void printTipChat() {
		if(TIPS.size() - 1 < TIP_COUNT)
			TIP_COUNT = 0;
		
		String tip = TIPS.get(TIP_COUNT);
		TIP_COUNT++;
		if (tip != "" || tip != null)
			Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "[TIP] " + tip);
	}
}