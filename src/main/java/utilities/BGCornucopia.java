package utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import main.BGMain;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class BGCornucopia {
	private static Block mainBlock;
	private static Integer radius = 10;
	private static Logger log = BGMain.getLog();
	private static Chest[] chests = new Chest[8];
	
	private static ArrayList<Location> cblocks = new ArrayList<>();

	
	public static void createCorn() {
		BGCornucopia.mainBlock = BGCornucopia.getCornSpawnBlock();
		mainBlock.setType(Material.DIAMOND_BLOCK);
		cblocks.add(mainBlock.getLocation());
		removeAbove(mainBlock);
		createFloor(Material.GOLD_BLOCK);
		
		if(BGMain.CORNUCOPIA_CHESTS)
			createCornucopia();
	}
	
	private static Block getCornSpawnBlock() {
		Location loc = BGMain.getSpawn().subtract(0, 2.5, 0);
		Block b = loc.getWorld().getBlockAt(loc);
		Integer sub = 0;
		while(b.getType() == Material.LOG || b.getType() == Material.LEAVES) {
			sub++;
			loc = BGMain.getSpawn().subtract(0, 2.5 + sub, 0);
			b = loc.getWorld().getBlockAt(loc);
		}
		return b;
	}
	
	private static void createFloor(Material m) {
		Location loc = mainBlock.getLocation();
		Integer r = radius;
	               
		log.info("Generating the cornucopia.");	    
	    
	    for (double x = -r; x <= r; x++) {
	        for (double z = -r; z <= r; z++) {
	        	Location l = new Location(Bukkit.getServer().getWorlds().get(0), loc.getX() + x, loc.getY(), loc.getZ() + z);
	        	if(l.distance(loc) <= r && l.getBlock().getType() != Material.DIAMOND_BLOCK) {
	        		removeAbove(l.getBlock());
	        		l.getBlock().setType(m);
	        		cblocks.add(l);
	        	}
	        }
	    }
	    
	}
	
	private static void createCornucopia() {
		Location loc = mainBlock.getLocation();
		loc.add(-3, 1, -3);
		Integer curchest = 0;
		
		//-2: new layer; -1: new row; 0: air; 1: block; 
		// 2: chest; 3: enchanting table; 4: fence; 5 : no change
		// 6: diamond_block; 7: beacon
		Integer[] co = {0, 0, 0, 0, 0, 0, 0, -1,
						0, 4, 2, 1, 2, 4, 0, -1,
						0, 2, 1, 1, 1, 2, 0, -1,
						0, 1, 1, 1, 1, 1, 0, -1,
						0, 2, 1, 1, 1, 2, 0, -1,
						0, 4, 2, 1, 2, 4, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -2,
						
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 4, 0, 0, 0, 4, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 0, 0, 3, 0, 0, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 4, 0, 0, 0, 4, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -2,
						
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 4, 0, 0, 0, 4, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 4, 0, 0, 0, 4, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -2,
						
						0, 0, 1, 1, 1, 0, 0, -1,
						0, 1, 1, 1, 1, 1, 0, -1,
						1, 1, 0, 0, 0, 1, 1, -1,
						1, 1, 0, 0, 0, 1, 1, -1,
						1, 1, 0, 0, 0, 1, 1, -1,
						0, 1, 1, 1, 1, 1, 0, -1,
						0, 0, 1, 1, 1, 0, 0, -2,
						
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 0, 0, 1, 0, 0, 0, -1,
						0, 0, 1, 1, 1, 0, 0, -1,
						0, 1, 1, 0, 1, 1, 0, -1,
						0, 0, 1, 1, 1, 0, 0, -1,
						0, 0, 0, 1, 0, 0, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -2,
						
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 0, 0, 1, 0, 0, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -1,
						0, 0, 0, 0, 0, 0, 0, -2};
		
		for(Integer i : co) {
			Material m = Material.AIR;
			switch (i) {
			case 0:
				m = Material.AIR;
				break;
			case 1:
				m = Material.IRON_BLOCK;
				break;
			case 2:
				m = Material.CHEST;
				break;
			case 3:
				m = Material.ENCHANTMENT_TABLE;
				break;
			case 4:
				m = Material.FENCE;
				break;
			case 5:
				break;
			case -1:
				break;
			case -2:
				break;
			default:
				log.warning("Illegal integer found while creating cornucopia: " + i.toString());
				break;
			}
			
			if(i == -1) {
				loc.add(0, 0, 1);
				loc.subtract(7, 0, 0);
			} else if(i == -2) {
				loc.add(0, 1, 0);
				loc.subtract(7, 0, 6);
			} else if(i == 5){
				loc.add(1, 0, 0);
			}else {
				loc.getBlock().setType(m);
				if(i != 0)
					cblocks.add(loc.getBlock().getLocation());
				if(m == Material.CHEST) {
					chests[curchest] = (Chest) loc.getBlock().getState();
					if(curchest < 8) curchest++;
				}
				loc.add(1, 0, 0);
			}
		}
	}
	
	public static Boolean isCornucopiaBlock(Block b) {		
		if(!BGMain.CORNUCOPIA)
			return false;

		return cblocks.contains(b.getLocation());
	}
	
	
	public static void removeAbove(Block block) {
		Location loc = block.getLocation();
		loc.setY(loc.getY()+1);
		Block newBlock = Bukkit.getServer().getWorlds().get(0).getBlockAt(loc);
		while(loc.getY() < Bukkit.getServer().getWorlds().get(0).getMaxHeight()) {
			newBlock.setType(Material.AIR);
			loc.setY(loc.getY()+1);
			newBlock = Bukkit.getServer().getWorlds().get(0).getBlockAt(loc);
		}
	}
	
	public static void spawnItems() {
		List<String> items = BGFiles.cornconf.getStringList("ITEMS");
		for(String item : items) {
			String[] oneitem = item.split(",");
			Random r = new Random();
			String itemid = oneitem[0];
			Integer minamount = Integer.parseInt(oneitem[1]);
			Integer maxamount = Integer.parseInt(oneitem[2]);
			Integer amount = 0;
			Boolean force = Boolean.parseBoolean(oneitem[3]);
			Boolean spawn = force;
			Integer id = null;
			Short durability = null;
			
			if(!force)
				spawn = r.nextBoolean();
			
			if(!spawn)
				continue;
			
			if (item.contains(":")) {
				String[] it = itemid.split(":");
				id = Integer.parseInt(it[0]);
				durability = Short.parseShort(it[1]);
			} else {
				id = Integer.parseInt(itemid);
			}
			
			ItemStack i = new ItemStack(id, 1);
			
			if(durability != null)
				i.setDurability(durability);
			
			if(oneitem.length == 6)
				i.addUnsafeEnchantment(Enchantment.getById(Integer.parseInt(oneitem[4])), Integer.parseInt(oneitem[5]));
						
			Integer ra = radius;
			
			if(maxamount == minamount)
				amount = maxamount;
			else
				amount = minamount + r.nextInt(maxamount - minamount + 1);
			
			if(BGMain.CORNUCOPIA_CHESTS) {				
				while(amount > 0) {
					Chest chest = chests[r.nextInt(8)];
					Integer slot = r.nextInt(27);
					int maxtry = 0;
					while(chest.getInventory().getItem(slot) != null &&!chest.getInventory().getItem(slot).getType().equals(i.getType()) && maxtry < 500) {
						maxtry++;
						slot = r.nextInt(27);
					}
					if(chest.getInventory().getItem(slot) != null)
						i.setAmount(i.getAmount() + 1);
					chest.getInventory().setItem(slot, i);
					chest.update();
					amount--;
				}
			} else {
				Location c = mainBlock.getLocation();
				c.add(-(ra/2) + r.nextInt(ra), 1, -(ra/2) + r.nextInt(ra));
				while(amount > 0) {
					Bukkit.getServer().getWorlds().get(0).dropItemNaturally(c, i).setPickupDelay(20 * 5);
					amount--;
				}
			}
		}
	}
	
	public static Block getMainBlock() {
		return mainBlock;
	}
 }
