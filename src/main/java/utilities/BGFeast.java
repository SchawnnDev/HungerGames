package utilities;

import java.text.DecimalFormat;
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

public class BGFeast {

	private static Block mainBlock = null;
	private static Integer radius = 8;
	private static Logger log = BGMain.getLog();
	private static Boolean spawned = false;
	private static Chest[] chests = new Chest[8];
	
	private static ArrayList<Location> fblocks = new ArrayList<>();

	public static void announceFeast(Integer time) {
		if(mainBlock == null) {
			do {
				mainBlock = BGMain.getRandomLocation().subtract(0, 1, 0).getBlock();
			} while (!BGMain.inBorder(mainBlock.getLocation()) || mainBlock.getType() == Material.LOG || mainBlock.getType() == Material.LEAVES);
			mainBlock.setType(Material.NETHERRACK);
			fblocks.add(mainBlock.getLocation());
			removeAbove(mainBlock);
			createFeast(Material.SOUL_SAND);
			spawned = true;
		}
		
		String s = "";
		if(time > 1)
			s = "s";
		
		DecimalFormat df = new DecimalFormat("##.#");
		BGChat.printInfoChat("Feast will spawn in " + time + " minute" + s + " at X: " + df.format(mainBlock.getLocation().getX()) + " | Y: " + df.format(mainBlock.getLocation().getY()) + " | Z: " + df.format(mainBlock.getLocation().getZ()));
	}
	
	public static void spawnFeast() {
		if(mainBlock == null)
			announceFeast(0);
		
		DecimalFormat df = new DecimalFormat("##.#");
		BGChat.printInfoChat("Feast spawned at X: " + df.format(mainBlock.getLocation().getX()) + " | Y: " + df.format(mainBlock.getLocation().getY()) + " | Z: " + df.format(mainBlock.getLocation().getZ()));
		if(BGMain.FEAST_CHESTS)
			spawnChests();
		
		List<String> items = BGFiles.feastconf.getStringList("ITEMS");
		for(String item : items) {
			String[] oneitem = item.split(",");
			Random r = new Random();
			String itemid = oneitem[0];
			int minamount = Integer.parseInt(oneitem[1]);
            int maxamount = Integer.parseInt(oneitem[2]);
            int amount = 0;
			boolean force = Boolean.parseBoolean(oneitem[3]);
            boolean spawn = force;
			int id = 0;
			Short durability = 0;
			
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
						
			int ra = radius;
			
			if(maxamount == minamount)
				amount = maxamount;
			else
				amount = minamount + r.nextInt(maxamount - minamount + 1);
			
			if(BGMain.FEAST_CHESTS) {				
				while(amount > 0) {
					Chest chest = chests[r.nextInt(8)];
					int slot = r.nextInt(27);
					int maxtry = 0;
					while(chest.getInventory().getItem(slot) != null 
							&& !chest.getInventory().getItem(slot).getType().equals(i.getType()) && maxtry < 1000)
						slot = r.nextInt(27);
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
	
	public static Boolean isFeastBlock(Block b) {		
		if(!BGMain.FEAST || !spawned)
			return false;

		return fblocks.contains(b.getLocation());
	}
	
	private static void createFeast(Material m) {
		Location loc = mainBlock.getLocation();
		Integer r = radius;
	               
		log.info("Generating feast.");	    
	    
	    for (double x = -r; x <= r; x++) {
	        for (double z = -r; z <= r; z++) {
	        	Location l = new Location(Bukkit.getServer().getWorlds().get(0), loc.getX() + x, loc.getY(), loc.getZ() + z);
	        	if(l.distance(loc) <= r && l.getBlock().getType() != Material.NETHERRACK) {
	        		removeAbove(l.getBlock());
	        		l.getBlock().setType(m);
	        		fblocks.add(l);
	        	}
	        }
	    }
	    
	    spawnFramework();
	}
	
	private static void spawnFramework() {
		Location loc = mainBlock.getLocation();
		loc.add(-3, 1, -3);
		
		//-2: new layer; -1: new row; 0: air; 1: block; 
		// 2: chest; 3: enchanting table; 4: fence; 5 : no change;
		// 6: diamond_block; 7: beacon
		Integer[] co = {0, 0, 0, 0, 0, 0, 0, -1,
				0, 4, 0, 1, 0, 4, 0, -1,
				0, 0, 1, 1, 1, 0, 0, -1,
				0, 1, 1, 1, 1, 1, 0, -1,
				0, 0, 1, 1, 1, 0, 0, -1,
				0, 4, 0, 1, 0, 4, 0, -1,
				0, 0, 0, 0, 0, 0, 0, -2,
				
				0, 0, 0, 0, 0, 0, 0, -1,
				0, 4, 0, 0, 0, 4, 0, -1,
				0, 0, 0, 0, 0, 0, 0, -1,
				0, 0, 0, 0, 0, 0, 0, -1,
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
				
				0, 0, 6, 6, 6, 0, 0, -1,
				0, 6, 6, 6, 6, 6, 0, -1,
				6, 6, 0, 0, 0, 6, 6, -1,
				6, 6, 0, 0, 0, 6, 6, -1,
				6, 6, 0, 0, 0, 6, 6, -1,
				0, 6, 6, 6, 6, 6, 0, -1,
				0, 0, 6, 6, 6, 0, 0, -2,
				
				0, 0, 0, 0, 0, 0, 0, -1,
				0, 0, 0, 6, 0, 0, 0, -1,
				0, 0, 6, 6, 6, 0, 0, -1,
				0, 6, 6, 6, 6, 6, 0, -1,
				0, 0, 6, 6, 6, 0, 0, -1,
				0, 0, 0, 6, 0, 0, 0, -1,
				0, 0, 0, 0, 0, 0, 0, -2,
				
				0, 0, 0, 0, 0, 0, 0, -1,
				0, 0, 0, 0, 0, 0, 0, -1,
				0, 0, 0, 0, 0, 0, 0, -1,
				0, 0, 0, 7, 0, 0, 0, -1,
				0, 0, 0, 0, 0, 0, 0, -1,
				0, 0, 0, 0, 0, 0, 0, -1,
				0, 0, 0, 0, 0, 0, 0, -2,};
		
		for(Integer i : co) {
			Material m = Material.AIR;
			switch (i) {
			case 0:
				m = Material.AIR;
				break;
			case 1:
				m = Material.OBSIDIAN;
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
			case 6:
				m = Material.DIAMOND_BLOCK;
				break;
			case 7:
				m = Material.BEACON;
				break;
			case -1:
				break;
			case -2:
				break;
			default:
				log.warning("Illegal integer found while creating Chests at Feast: " + i.toString());
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
					fblocks.add(loc.getBlock().getLocation());
				loc.add(1, 0, 0);
			}
		}
	}
	
	private static void spawnChests() {
		Location loc = mainBlock.getLocation();
		loc.add(-3, 1, -3);
		Integer curchest = 0;
		
		//-2: new layer; -1: new row; 0: air; 1: block; 
		// 2: chest; 3: enchanting table; 4: fence; 5 : no change;
		// 6: diamond_block; 7: beacon
		Integer[] co = {5, 5, 5, 5, 5, 5, 5, -1,
				5, 5, 2, 5, 2, 5, 5, -1,
				5, 2, 5, 5, 5, 2, 5, -1,
				5, 5, 5, 5, 5, 5, 5, -1,
				5, 2, 5, 5, 5, 2, 5, -1,
				5, 5, 2, 5, 2, 5, 5, -1,
				5, 5, 5, 5, 5, 5, 5, -2,
				
				5, 5, 5, 5, 5, 5, 5, -1,
				5, 5, 5, 5, 5, 5, 5, -1,
				5, 5, 5, 5, 5, 5, 5, -1,
				5, 5, 5, 3, 5, 5, 5, -1,
				5, 5, 5, 5, 5, 5, 5, -1,
				5, 5, 5, 5, 5, 5, 5, -1,
				5, 5, 5, 5, 5, 5, 5, -2};
		
		for(Integer i : co) {
			Material m = Material.AIR;
			switch (i) {
			case 0:
				m = Material.AIR;
				break;
			case 1:
				m = Material.OBSIDIAN;
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
			case 6:
				m = Material.DIAMOND_BLOCK;
				break;
			case 7:
				m = Material.BEACON;
				break;
			case -1:
				break;
			case -2:
				break;
			default:
				log.warning("Illegal integer found while creating Chests at Feast: " + i.toString());
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
					fblocks.add(loc.getBlock().getLocation());
				if(m == Material.CHEST) {
					chests[curchest] = (Chest) loc.getBlock().getState();
					if(curchest < 8) curchest++;
				}
				loc.add(1, 0, 0);
			}
		}
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
	
	public static Block getMainBlock() {
		return mainBlock;
	}
}
