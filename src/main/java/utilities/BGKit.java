package utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import main.BGMain;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BGKit {
	static Logger log = BGMain.getPluginLogger();
	static HashMap<Player, String> KIT = new HashMap<Player, String>();
	public static ArrayList<String> kits = new ArrayList<String>();
	private static HashMap<Integer, String> ABILITY_DESC = new HashMap<Integer, String>();

	public BGKit() {
		Set<String> kitList = BGFiles.kitconf.getKeys(false);
		for(String kit : kitList) {
			if(kit.equalsIgnoreCase("default"))
				continue;
			
			kits.add(kit.toLowerCase());
		}
		
		ABILITY_DESC.put(1, BGFiles.abconf.getString("AB.1.Desc"));
		ABILITY_DESC.put(2, BGFiles.abconf.getString("AB.2.Desc"));
		ABILITY_DESC.put(3, BGFiles.abconf.getString("AB.3.Desc"));
		ABILITY_DESC.put(4, BGFiles.abconf.getString("AB.4.Desc"));
		ABILITY_DESC.put(5, BGFiles.abconf.getString("AB.5.Desc"));
		ABILITY_DESC.put(6, BGFiles.abconf.getString("AB.6.Desc"));
		ABILITY_DESC.put(7, BGFiles.abconf.getString("AB.7.Desc"));
		ABILITY_DESC.put(8, BGFiles.abconf.getString("AB.8.Desc"));
		ABILITY_DESC.put(9, BGFiles.abconf.getString("AB.9.Desc"));
		ABILITY_DESC.put(10, BGFiles.abconf.getString("AB.10.Desc"));
		ABILITY_DESC.put(11, BGFiles.abconf.getString("AB.11.Desc"));
		ABILITY_DESC.put(12, BGFiles.abconf.getString("AB.12.Desc"));
		ABILITY_DESC.put(13, BGFiles.abconf.getString("AB.13.Desc"));
		ABILITY_DESC.put(14, BGFiles.abconf.getString("AB.14.Desc"));
		ABILITY_DESC.put(15, BGFiles.abconf.getString("AB.15.Desc"));
		ABILITY_DESC.put(16, BGFiles.abconf.getString("AB.16.Desc"));
		
		if(BGMain.ADV_ABI) {
			ABILITY_DESC.put(17, BGFiles.abconf.getString("AB.17.Desc"));
		}else {
			ABILITY_DESC.put(17, "Advanced Abilities disabled! This ability wont work!");
		}
		
		
		ABILITY_DESC.put(18, BGFiles.abconf.getString("AB.18.Desc"));
		ABILITY_DESC.put(19, BGFiles.abconf.getString("AB.19.Desc"));
		ABILITY_DESC.put(20, BGFiles.abconf.getString("AB.20.Desc"));
		ABILITY_DESC.put(21, BGFiles.abconf.getString("AB.21.Desc"));
		ABILITY_DESC.put(22, BGFiles.abconf.getString("AB.22.Desc"));
		ABILITY_DESC.put(23, BGFiles.abconf.getString("AB.23.Desc"));
		
	}

	public static void giveKit(Player p) {
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setExp(0);
		p.setLevel(0);
		
		if (!KIT.containsKey(p)) {
			if (BGMain.COMPASS.booleanValue()) {
				p.getInventory().addItem(
						new ItemStack[] { new ItemStack(Material.COMPASS, 1) });
			}
			
			if (BGMain.DEFAULT_KIT) {
				try {
				ConfigurationSection def = BGFiles.kitconf.getConfigurationSection("default");
				setKitDisplayName(p, "Default");
				
				List<String> kititems = def.getStringList("ITEMS");
				for (String item : kititems) {
					String[] oneitem = item.split(",");
					ItemStack i = null;
					Integer id = null;
					Integer amount = null;
					Short durability = null;
					if (oneitem[0].contains(":")) {
						String[] ITEM_ID = oneitem[0].split(":");
						id = Integer.valueOf(Integer.parseInt(ITEM_ID[0]));
						amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
						durability = Short.valueOf(Short.parseShort(ITEM_ID[1]));
						i = new ItemStack(id.intValue(), amount.intValue(),
								durability.shortValue());
					} else {
						id = Integer.valueOf(Integer.parseInt(oneitem[0]));
						amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
						i = new ItemStack(id.intValue(), amount.intValue());
					}

					if (oneitem.length == 4) {
						i.addUnsafeEnchantment(
								Enchantment.getById(Integer.parseInt(oneitem[2])),
								Integer.parseInt(oneitem[3]));
					}

					if ((id.intValue() < 298) || (317 < id.intValue())) {
						p.getInventory().addItem(new ItemStack[] { i });
					} else if ((id.intValue() == 298) || (id.intValue() == 302)
							|| (id.intValue() == 306) || (id.intValue() == 310)
							|| (id.intValue() == 314)) {
						i.setAmount(1);
						p.getInventory().setHelmet(i);
					} else if ((id.intValue() == 299) || (id.intValue() == 303)
							|| (id.intValue() == 307) || (id.intValue() == 311)
							|| (id.intValue() == 315)) {
						i.setAmount(1);
						p.getInventory().setChestplate(i);
					} else if ((id.intValue() == 300) || (id.intValue() == 304)
							|| (id.intValue() == 308) || (id.intValue() == 312)
							|| (id.intValue() == 316)) {
						i.setAmount(1);
						p.getInventory().setLeggings(i);
					} else if ((id.intValue() == 301) || (id.intValue() == 305)
							|| (id.intValue() == 309) || (id.intValue() == 313)
							|| (id.intValue() == 317)) {
						i.setAmount(1);
						p.getInventory().setBoots(i);
					}
				}

				List<String> pots = def.getStringList("POTION");
				for(String pot : pots) {	
					if (pot != null & pot != "") {
						if (!pot.equals(0)) {
							String[] potion = pot.split(",");
							if (Integer.parseInt(potion[0]) != 0) {
								if (Integer.parseInt(potion[1]) == 0) {
									p.addPotionEffect(new PotionEffect(PotionEffectType
											.getById(Integer.parseInt(potion[0])),
											BGMain.MAX_GAME_RUNNING_TIME * 1200, Integer
											.parseInt(potion[2])));
								} else {
									p.addPotionEffect(new PotionEffect(PotionEffectType
											.getById(Integer.parseInt(potion[0])), Integer
											.parseInt(potion[1]) * 20, Integer
											.parseInt(potion[2])));
								}
							}
						}
					}
				}
			} catch(Exception ex) {
				log.warning("[BukkitGames] Error while trying to give kit 'default' to player '" + p.getName() + "'!");
				log.warning(ex.getMessage());
			}
		} 
			
			return;
		}

		String kitname = (String) KIT.get(p);
		try {
		ConfigurationSection kit = BGFiles.kitconf.getConfigurationSection(kitname.toLowerCase());

		List<String> kititems = kit.getStringList("ITEMS");
		for (String item : kititems) {
			String[] oneitem = item.split(",");
			ItemStack i = null;
			Integer id = null;
			Integer amount = null;
			Short durability = null;
			int blue = 0;
			int green = 0;
			int red = 0;
			if (item.toLowerCase().contains(">")){
			String[] color = item.split(">");
			if (color[1].toLowerCase().contains("blue"))
			{
			blue = 255;
			green = 0;
			red = 0;
			}
			else if (color[1].toLowerCase().contains("green"))
			{
			blue = 0;
			green = 255;
			red = 0;
			}
			else if (color[1].toLowerCase().contains("red"))
			{
			blue = 0;
			green = 0;
			red = 255;
			}
			else if (color[1].toLowerCase().contains("black"))
			{
			blue = 0;
			green = 0;
			red = 0;
			}
			else if (color[1].toLowerCase().contains("white"))
			{
			blue = 255;
			green = 255;
			red = 255;
			}
			}
			if (oneitem[0].contains(":")) {
				String[] ITEM_ID = oneitem[0].split(":");
				id = Integer.valueOf(Integer.parseInt(ITEM_ID[0]));
				amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
				durability = Short.valueOf(Short.parseShort(ITEM_ID[1]));
				i = new ItemStack(id.intValue(), amount.intValue(),
						durability.shortValue());
			} else {
				id = Integer.valueOf(Integer.parseInt(oneitem[0]));
				amount = Integer.valueOf(Integer.parseInt(oneitem[1]));
				i = new ItemStack(id.intValue(), amount.intValue());
			}

			if (oneitem.length == 4) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
			}
			// For-Loops did not seem to work, so its written out manually.
			else if (oneitem.length == 6) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
			}
			else if (oneitem.length == 8) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[6])),
						Integer.parseInt(oneitem[7]));
			}
			else if (oneitem.length == 10) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[6])),
						Integer.parseInt(oneitem[7]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[8])),
						Integer.parseInt(oneitem[9]));

			}
			else if (oneitem.length == 12) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[6])),
						Integer.parseInt(oneitem[7]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[8])),
						Integer.parseInt(oneitem[9]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[10])),
						Integer.parseInt(oneitem[11]));
			}
			else if (oneitem.length == 14) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[6])),
						Integer.parseInt(oneitem[7]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[8])),
						Integer.parseInt(oneitem[9]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[10])),
						Integer.parseInt(oneitem[11]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[12])),
						Integer.parseInt(oneitem[13]));
			}
			else if (oneitem.length == 16) {
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[2])),
						Integer.parseInt(oneitem[3]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[4])),
						Integer.parseInt(oneitem[5]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[6])),
						Integer.parseInt(oneitem[7]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[8])),
						Integer.parseInt(oneitem[9]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[10])),
						Integer.parseInt(oneitem[11]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[12])),
						Integer.parseInt(oneitem[13]));
				i.addUnsafeEnchantment(
						Enchantment.getById(Integer.parseInt(oneitem[14])),
						Integer.parseInt(oneitem[15]));
			}

			if ((id.intValue() < 298) || (317 < id.intValue())) 
			{
				p.getInventory().addItem(new ItemStack[] { i });
			} 
			else if ((id.intValue() == 298) || (id.intValue() == 302)
					|| (id.intValue() == 306) || (id.intValue() == 310)
					|| (id.intValue() == 314)) 
			{
				if ((id.intValue() == 298) && (item.contains(">")))// Checks if the Item line contains a > to give the armor color.
				{
				    LeatherArmorMeta h = (LeatherArmorMeta)i.getItemMeta();
				    h.setColor(Color.fromBGR(blue, green, red));//Gave it variables so they set-up above.
				    i.setItemMeta(h);// Give the armor the actual color we made
				}
				i.setAmount(1);
				p.getInventory().setHelmet(i);
				}
			else if ((id.intValue() == 299) || (id.intValue() == 303)
					|| (id.intValue() == 307) || (id.intValue() == 311)
					|| (id.intValue() == 315)) 
			{
				if ((id.intValue() == 299) && (item.contains(">")))
				{
				    LeatherArmorMeta c = (LeatherArmorMeta)i.getItemMeta();
				    c.setColor(Color.fromBGR(blue, green, red));
				    i.setItemMeta(c);
				}
				i.setAmount(1);
				p.getInventory().setChestplate(i);
			} 
			else if ((id.intValue() == 300) || (id.intValue() == 304)
					|| (id.intValue() == 308) || (id.intValue() == 312)
					|| (id.intValue() == 316)) 
			{
				if ((id.intValue() == 300) && (item.contains(">")))
				{
				    LeatherArmorMeta l = (LeatherArmorMeta)i.getItemMeta();
				    l.setColor(Color.fromBGR(blue, green, red));
				    i.setItemMeta(l);
				}
				i.setAmount(1);
				p.getInventory().setLeggings(i);
				}
			else if ((id.intValue() == 301) || (id.intValue() == 305)
					|| (id.intValue() == 309) || (id.intValue() == 313)
					|| (id.intValue() == 317)) 
			{
				if ((id.intValue() == 301) && (item.contains(">")))
				{
				    LeatherArmorMeta b = (LeatherArmorMeta)i.getItemMeta();
				    b.setColor(Color.fromBGR(blue, green, red));
				    i.setItemMeta(b);
				}
				i.setAmount(1);
				p.getInventory().setBoots(i);
			}
		}

		List<String> pots = kit.getStringList("POTION");
		for(String pot : pots) {	
			if (pot != null & pot != "") {
				if (!pot.equals(0)) {
					String[] potion = pot.split(",");
					if (Integer.parseInt(potion[0]) != 0) {
						if (Integer.parseInt(potion[1]) == 0) {
							p.addPotionEffect(new PotionEffect(PotionEffectType
									.getById(Integer.parseInt(potion[0])),
									BGMain.MAX_GAME_RUNNING_TIME * 1200, Integer
									.parseInt(potion[2])));
						} else {
							p.addPotionEffect(new PotionEffect(PotionEffectType
									.getById(Integer.parseInt(potion[0])), Integer
									.parseInt(potion[1]) * 20, Integer
									.parseInt(potion[2])));
						}
					}
				}
			}
		}

		if (BGMain.COMPASS.booleanValue())
			p.getInventory().addItem(
					new ItemStack[] { new ItemStack(Material.COMPASS, 1) });
		} catch(Exception ex) {
			log.warning("[BukkitGames] Error while trying to give kit '" + kitname + "' to player '" + p.getName() + "'!");
			log.warning(ex.getMessage());
		}
	}

	public static void setKit(Player player, String kitname) {
		kitname = kitname.toLowerCase();
		kitname = kitname.replace(".", "");
		ConfigurationSection kit = BGFiles.kitconf.getConfigurationSection(kitname);

		if (kit == null  && !kits.contains(kitname)) {
			BGChat.printPlayerChat(player, "That kit doesn't exist!");
			return;
		}
		if(KIT.get(player) == kitname)
			return;

		if (player.hasPermission("bg.kit." + kitname.toLowerCase())
				|| player.hasPermission("bg.kit.*") || (BGMain.SIMP_REW && BGMain.winner(player))
				|| (BGMain.REW && BGReward.BOUGHT_KITS.get(player.getName()) != null
					&& BGReward.BOUGHT_KITS.get(player.getName()).equals(kitname.toLowerCase()))) {
			if (KIT.containsKey(player)) {
				KIT.remove(player);
			}

			KIT.put(player, kitname);
			char[] stringArray = kitname.toCharArray();
			stringArray[0] = Character.toUpperCase(stringArray[0]);
			kitname = new String(stringArray);
			
			BGChat.printPlayerChat(player, "You have chosen " + kitname + " as your kit.");
			setKitDisplayName(player, kitname);

		} else {
			BGChat.printPlayerChat(player, BGMain.NO_KIT_MSG);
			return;
		}
	}

	private static void setKitDisplayName(Player player, String kitname) {
		if(!BGMain.KIT_PREFIX)
			return;
		
		if (BGMain.winner(player))
			player.setDisplayName(ChatColor.DARK_GRAY + "[" + kitname + "] " + ChatColor.RESET + ChatColor.GOLD
					+ player.getName() + ChatColor.WHITE);
		else if (player.hasPermission("bg.admin.color")
				|| player.hasPermission("bg.admin.*"))
			player.setDisplayName(ChatColor.DARK_GRAY +"[" + kitname + "] "+ ChatColor.RESET + ChatColor.RED
					+ player.getName() + ChatColor.WHITE);
		else if (player.hasPermission("bg.vip.color")
				|| player.hasPermission("bg.vip.*"))
			player.setDisplayName(ChatColor.DARK_GRAY +"[" + kitname + "] "+ ChatColor.RESET + ChatColor.BLUE
					+ player.getName() + ChatColor.WHITE);
		else
			player.setDisplayName(ChatColor.DARK_GRAY +"[" + kitname + "] " + ChatColor.RESET
					+ ChatColor.WHITE + player.getName() + ChatColor.WHITE);
	}
	
	public static Boolean hasAbility(Player player, Integer ability) {
		
		if (!KIT.containsKey(player)) {
			if (BGMain.DEFAULT_KIT) {
				ConfigurationSection def = BGFiles.kitconf.getConfigurationSection("default");
				List<Integer> s = def.getIntegerList("ABILITY");
				for(Integer i : s) {
					if (i.equals(ability)) {
					return true;
					}
				}
				return false;
			}else {
				return false;
			}
		}

		String kitname = getKit(player);
		ConfigurationSection kit = BGFiles.kitconf.getConfigurationSection(kitname);

		List<Integer> s = kit.getIntegerList("ABILITY");
		for(Integer i : s) {
			if (i.equals(ability)) {
				return true;
			}
		}
		return false;
	}

	public static String getKit(Player player) {
		if(!KIT.containsKey(player))
			return null;
		
		return KIT.get(player);
	}
	
	public static int getCoins(String kitName) {
		ConfigurationSection def = BGFiles.kitconf.getConfigurationSection(kitName);
		return def.getInt("COINS");
	}
	
	public static boolean isKit(String kitName) {
		return kits.contains(kitName);
	}
	
	public static String getAbilityDesc(Integer ability) {
		if (ability == 0)
			return null;

		if (ABILITY_DESC.containsKey(ability))
			return ABILITY_DESC.get(ability);
		else
			return null;
	}

	public static void setAbilityDesc(Integer ability, String description) throws Error {
		if (ABILITY_DESC.containsKey(ability))
			throw new Error("Cannot overwrite existing descriptions of abilties.");
		else
			ABILITY_DESC.put(ability, description);
	}
}
