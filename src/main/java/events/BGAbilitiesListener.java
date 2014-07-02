package events;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import utilities.BGChat;
import utilities.BGCooldown;
import utilities.BGFiles;
import utilities.BGKit;
import utilities.BGReward;
import utilities.BGTeam;
import utilities.enums.GameState;
import utilities.enums.Translation;

import main.BGMain;

public class BGAbilitiesListener implements Listener {

	Logger log = BGMain.getPluginLogger();
	
	public static ArrayList<Player> viperList = new ArrayList<Player>();
	public static ArrayList<Player> monkList = new ArrayList<Player>();
	public static ArrayList<Player> thiefList = new ArrayList<Player>();
	public static ArrayList<Player> ghostList = new ArrayList<Player>();
	public static ArrayList<Player> thorList = new ArrayList<Player>();
	public static ArrayList<Player> timeList = new ArrayList<Player>();
	public static ArrayList<Player> freezeList = new ArrayList<Player>();
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action a = event.getAction();
		if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
			if ((BGKit.hasAbility(p, Integer.valueOf(5)) & p.getItemInHand()
					.getType() == Material.COOKIE)) {
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.INCREASE_DAMAGE, BGFiles.abconf.getInt("AB.5.Duration") * 20, 0));
				p.getInventory().removeItem(
						new ItemStack[] { new ItemStack(Material.COOKIE, 1) });
				p.playSound(p.getLocation(), Sound.BURP, 1.0F, (byte) 1);
			}
		}
		
		if (a == Action.LEFT_CLICK_BLOCK || a == Action.LEFT_CLICK_AIR) {
			if (BGKit.hasAbility(p, Integer.valueOf(4)) && p.getItemInHand() != null && 
					p.getItemInHand().getType().equals(Material.FIREBALL)) {
				Vector lookat = p.getLocation().getDirection().multiply(10);
				Fireball fire = p.getWorld().spawn(p.getLocation().add(lookat), Fireball.class);
				fire.setShooter(p);
				p.playSound(p.getLocation(), Sound.FIRE, 1.0F, (byte) 1);
				p.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.FIREBALL, 1) });
			}
		}
		
		try{
			if (BGKit.hasAbility(p, 11) && a == Action.RIGHT_CLICK_BLOCK && p.getItemInHand()
					.getType() == Material.STONE_AXE) {
				if(!thorList.contains(p)) {
					thorList.add(p);
					BGCooldown.thorCooldown(p);
					Block block = event.getClickedBlock();
					Location loc = block.getLocation();
					World world = Bukkit.getServer().getWorlds().get(0);
					world.strikeLightning(loc);
				}else {
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.11.Expired"));
				}
			}
			
			if (BGKit.hasAbility(p, 16) && p.getItemInHand()
					.getType() == Material.APPLE && (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)) {
				
				if(!ghostList.contains(p)) {
					ghostList.add(p);
					BGCooldown.ghostCooldown(p);
										
					p.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.APPLE, 1) });
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, BGFiles.abconf.getInt("AB.16.Duration") * 20, 1));
					p.playSound(p.getLocation(), Sound.PORTAL_TRIGGER, 1.0F, (byte) 1);
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.16.invisible"));
				} else {
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.16.Expired"));
				}
			}
			
			if (BGKit.hasAbility(p, 21) && p.getItemInHand()
					.getType() == Material.POTATO && (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)) {
				
				p.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.POTATO, 1) });
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, BGFiles.abconf.getInt("AB.21.Duration") * 20, 1));
				p.playSound(p.getLocation(), Sound.ENDERMAN_IDLE, 1.0F, (byte) 1);
			}
			
			if(BGKit.hasAbility(p, 22) && BGMain.GAMESTATE == GameState.GAME && p.getItemInHand().getType() == Material.WATCH &&
					(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)) {
				
				if(!timeList.contains(p)) {
					timeList.add(p);
					BGCooldown.timeCooldown(p);
					
					p.getInventory().removeItem(new ItemStack[] {new ItemStack(Material.WATCH,1)});
					
					int radius = BGFiles.abconf.getInt("AB.22.radius");
					p.playSound(p.getLocation(), Sound.AMBIENCE_CAVE, 1.0F, (byte) 1);
					List<Entity> entities = p.getNearbyEntities(radius+30, radius+30, radius+30);
					for(Entity e : entities) {
						
						if(!e.getType().equals(EntityType.PLAYER) || BGMain.isSpectator((Player)e) || BGMain.isGameMaker((Player) e))
							continue;
						Player target = (Player) e;
						if(BGMain.TEAM) {
							
							if(BGTeam.isInTeam(p, target.getName()))
								continue;
						}
						if(p.getLocation().distance(target.getLocation()) < radius) {
							
							freezeList.add(target);
							String text = BGFiles.abconf.getString("AB.22.target");
							text = text.replace("<player>", p.getName());
							BGChat.printPlayerChat(target, text);
							BGCooldown.freezeCooldown(target);
							p.playSound(p.getLocation(), Sound.AMBIENCE_CAVE, 1.0F, (byte) 1);
						}	
					}
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.22.success"));
				}else {
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.22.Expired"));
				}
			}
		} catch(NullPointerException e) {
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile entity =  event.getEntity();

		if (entity.getType() == EntityType.ARROW) {
			Arrow arrow = (Arrow) entity;
			LivingEntity shooter = arrow.getShooter();
			if (shooter.getType() == EntityType.PLAYER) {
				Player player = (Player) shooter;
				if(BGMain.isSpectator(player)) {
					return;
				}
				if (BGKit.hasAbility(player, Integer.valueOf(1))) {
					Bukkit.getServer().getWorlds().get(0).createExplosion(arrow.getLocation(), 2.0F, false);
					arrow.remove();
				} else {
					return;
				}
			} else {
				return;
			}
		}

		if (entity.getType() == EntityType.SNOWBALL) {
			Snowball ball = (Snowball) entity;
			LivingEntity shooter = ball.getShooter();
			if (shooter.getType() == EntityType.PLAYER) {
				Player player = (Player) shooter;
				if(BGMain.isSpectator(player)) {
					return;
				}
				if (BGKit.hasAbility(player, Integer.valueOf(3)).booleanValue()) {
					Bukkit.getServer().getWorlds().get(0)
							.createExplosion(ball.getLocation(), 0.0F);
					for (Entity e : ball.getNearbyEntities(3.0D, 3.0D, 3.0D))
						if ((e.getType() == EntityType.PLAYER)) {
							Player pl = (Player) e;
							if (pl.getName() != player.getName()) {
								pl.addPotionEffect(new PotionEffect(
										PotionEffectType.BLINDNESS, 100, 1));
								pl.addPotionEffect(new PotionEffect(
										PotionEffectType.CONFUSION, 160, 1));
							}
						}
				}
			} else {
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent e) {
		Player p = e.getEntity().getKiller();
		if (BGKit.hasAbility(p, Integer.valueOf(7)).booleanValue()) {
			if (e.getEntityType() == EntityType.PIG) {
				e.getDrops().clear();
				e.getDrops().add(new ItemStack(Material.PORK, BGFiles.abconf.getInt("AB.7.Amount")));
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity(); //getEntity() returns the player...
		if(BGKit.hasAbility(p, 23)) {
			Bukkit.getServer().getWorlds().get(0).createExplosion(p.getLocation(), 2.5F, BGFiles.abconf.getBoolean("AB.23.Burn"));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent event) {		
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if(BGMain.isSpectator(p))
				return;
			if (BGKit.hasAbility(p, Integer.valueOf(8))) {
				if (event.getCause() == DamageCause.FALL) {
					if (event.getDamage() > 4) {
						event.setCancelled(true);
						p.damage(4);
					}
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
					List<Entity> nearbyEntities = event.getEntity().getNearbyEntities(5, 5, 5);
					for (Entity target : nearbyEntities) {
						if (target instanceof Player) {
							Player t = (Player) target;
							if(BGMain.isSpectator(t) || BGMain.isGameMaker(t))
								continue;
							if(BGMain.TEAM) {
								if(BGTeam.isInTeam(p, t.getName()))
									continue;
							}
							if(t.getName() == p.getName())
								continue;
							if (t.isSneaking())
								t.damage(event.getDamage() / 2, event.getEntity());
							else
								t.damage(event.getDamage(), event.getEntity());
						}
					}
				}
			}
			
			if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
				if ((BGKit.hasAbility(p, Integer.valueOf(6)).booleanValue() & !p
						.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))) {
					p.addPotionEffect(new PotionEffect(
							PotionEffectType.INCREASE_DAMAGE, 200, 1));
					p.addPotionEffect(new PotionEffect(
							PotionEffectType.FIRE_RESISTANCE, 260, 1));
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
				}
			}
				if(BGKit.hasAbility(p, 18)) {
					event.setDamage(event.getDamage() - 1);
				}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (BGKit.hasAbility(p, 2) && b.getType() == Material.LOG) {
			World w = Bukkit.getServer().getWorlds().get(0);
			Double y = b.getLocation().getY() + 1;
			Location l = new Location(w, b.getLocation().getX(), y, b
					.getLocation().getZ());
			while (l.getBlock().getType() == Material.LOG) {
				l.getBlock().breakNaturally();
				y++;
				l.setY(y);
			}
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		Player p = event.getPlayer();
		if (BGKit.hasAbility(p, 10) && block.getType() == Material.CROPS) {
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
			block.setData(CropState.RIPE.getData());
		}
		if (BGKit.hasAbility(p, 10) && block.getType() == Material.MELON_SEEDS) {
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
			block.setData(CropState.RIPE.getData());
		}
		if (BGKit.hasAbility(p, 10) && block.getType() == Material.PUMPKIN_SEEDS) {
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
			block.setData(CropState.RIPE.getData());
		}
		if (BGKit.hasAbility(p, 10) && block.getType() == Material.SAPLING) {
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
			TreeType t = getTree(block.getData());
			Bukkit.getServer().getWorlds().get(0).generateTree(block.getLocation(), t);
		}
	}
	
    public TreeType getTree(int data) {
        TreeType tretyp = TreeType.TREE;
        switch(data) {
        case 0:
            tretyp = TreeType.TREE;
            break;
        case 1:
            tretyp = TreeType.REDWOOD;
            break;
        case 2:
            tretyp = TreeType.BIRCH;
            break;
        case 3:
            tretyp = TreeType.JUNGLE;
            break;
        default:
            tretyp = TreeType.TREE;
        }
        return tretyp;
    }
    
    
    
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity defender = event.getEntity();
		
		if (BGMain.GAMESTATE == GameState.PREGAME && !(event.getEntity() instanceof Player)) {
			return;
		}
		if (BGMain.GAMESTATE != GameState.GAME && event.getEntity() instanceof Player) {
			return;
		}
		
		if (event.getEntity().isDead()) {
			return;
		}

		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				Player p = (Player) arrow.getShooter();
				if (!BGKit.hasAbility(p, 9)) {
					return;
				}
				if (p.getLocation().distance(event.getEntity().getLocation()) >= BGFiles.abconf.getInt("AB.9.Distance")) {
					if (event.getEntity() instanceof LivingEntity) {
						LivingEntity victom = (LivingEntity) event.getEntity();
						if (victom instanceof Player) {
							Player v = (Player) victom;
							ItemStack helmet = v.getInventory().getHelmet();
							if (helmet == null) {
								BGChat.printDeathChat(Translation.HEADSHOT_DEATH.t().replace("<victom>", v.getName()).replace("<player>", p.getName()));
								p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
								if (!BGMain.ADV_CHAT_SYSTEM) {
									BGChat.printDeathChat(Translation.PLAYERS_REMAIN.t().replace("<amount>", (BGMain.getGamers().length - 1) + ""));
									BGChat.printDeathChat("");
								}
								Location light = v.getLocation();
								Bukkit.getServer()
										.getWorlds().get(0)
										.strikeLightningEffect(
												light.add(0.0D, 100.0D, 0.0D));
								BGGameListener.last_headshot = v.getName();
								v.setHealth(0);
								v.kickPlayer(ChatColor.RED + Translation.HEADSHOT_DEATH.t().replace("<victom>", v.getName()).replace("<player>", p.getName()));
								
								if(BGMain.REW && BGMain.COINS_FOR_KILL != 0){
									BGReward.giveCoins(p.getName(), BGMain.COINS_FOR_KILL);
									if(BGMain.COINS_FOR_KILL == 1)
										BGChat.printPlayerChat(p, "You got 1 Coin for killing "+v.getName());
									else
										BGChat.printPlayerChat(p, "You got "+BGMain.COINS_FOR_KILL+" Coins for killing "+v.getName());
								}
								if (BGMain.SQL_USE) {
									Integer PL_ID = BGMain.getPlayerID(v
											.getName());
									Integer KL_ID = BGMain.getPlayerID(p
											.getName());
									BGMain.SQLquery("UPDATE `PLAYS` SET deathtime = NOW(), `REF_KILLER` = "
											+ KL_ID
											+ ", `DEATH_REASON` = 'HEADSHOT' WHERE `REF_PLAYER` = "
											+ PL_ID
											+ " AND `REF_GAME` = "
											+ BGMain.SQL_GAMEID + " ;");
								}
							} else {
								helmet.setDurability((short) (helmet.getDurability() + 20));
								v.getInventory().setHelmet(helmet);
							}
						} else {
							BGChat.printPlayerChat(p, Translation.HEADSHOT.t());
							victom.setHealth(0);
						}
					}
				}
			}
		}
		
		if (damager instanceof Player) {
			
			Player dam = (Player)damager;
			if(BGKit.hasAbility(dam, 12)) {
				
				if (dam.getHealth() == 20) {
					return;
				}
				
				dam.setHealth(dam.getHealth()+1);
			}
			
			if (defender.getType() == EntityType.PLAYER) {
				
				Player def = (Player)defender;
				
				if(BGKit.hasAbility(dam, 13) && dam.getItemInHand().getType() == Material.BLAZE_ROD && def.getItemInHand() != null) {
				
					if (!monkList.contains(dam)) {
						
						int random = (int) (Math.random()* (BGFiles.abconf.getInt("AB.13.Chance")-1)+1);
						if (random == 1) {
							monkList.add(dam);
							BGCooldown.monkCooldown(dam);
							def.getInventory().clear(def.getInventory().getHeldItemSlot());
							BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.13.Success"));
							BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.13.Success"));
							dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
						}
					}else {
						
						BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.13.Expired"));
					}
				}
				
				if(BGKit.hasAbility(dam, 15) && dam.getItemInHand().getType() == Material.STICK && def.getItemInHand() != null) {
					
					if(!thiefList.contains(dam)) {
						int random = (int) (Math.random()* (BGFiles.abconf.getInt("AB.15.Chance")-1)+1);
						if(random == 1) {
							thiefList.add(dam);
							BGCooldown.thiefCooldown(dam);
							dam.getInventory().clear(dam.getInventory().getHeldItemSlot());
							dam.getInventory().addItem(def.getItemInHand());
							def.getInventory().clear(def.getInventory().getHeldItemSlot());
							BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.15.Success"));
							BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.15.Success"));
							dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
						}
					}else {
						
						BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.15.Expired"));
					}
				}
				
				if (BGKit.hasAbility(dam, 19)) {
					
					int random = (int) (Math.random()* (BGFiles.abconf.getInt("AB.19.Chance")-1)+1);
					if(random == 1 && !viperList.contains(def)) {
						
						def.addPotionEffect(new PotionEffect(PotionEffectType.POISON, BGFiles.abconf.getInt("AB.19.Duration")*20, 1));
						viperList.add(def);
						BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.19.Damager"));
						BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.19.Defender"));
						BGCooldown.viperCooldown(def);
						dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
					}
				}
			}
								
			if (BGKit.hasAbility(dam, 18) && dam.getItemInHand().getType() == Material.AIR) {
				event.setDamage(event.getDamage()+ 4);
			}
		}
	}	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		if(freezeList.contains(p)) {
			event.setCancelled(true);
			BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.22.move"));
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event) {
		Entity entity = event.getTarget();
		if (entity != null) {
			if (entity instanceof Player) {
				Player player = (Player)entity;
				if(BGKit.hasAbility(player, 20) && event.getReason() == TargetReason.CLOSEST_PLAYER) {
					event.setCancelled(true);
				}
			}
		}
	}
}
