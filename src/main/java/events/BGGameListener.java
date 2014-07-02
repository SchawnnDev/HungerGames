package events;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import main.BGMain;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.Vector;

import utilities.BGChat;
import utilities.BGCornucopia;
import utilities.BGFeast;
import utilities.BGFiles;
import utilities.BGKit;
import utilities.BGReward;
import utilities.BGSign;
import utilities.BGTeam;
import utilities.BGVanish;
import utilities.enums.BorderType;
import utilities.enums.GameState;
import utilities.enums.Translation;

public class BGGameListener implements Listener {
	Logger log = BGMain.getPluginLogger();
	public static String last_quit;
	public static String last_headshot;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if(BGMain.isSpectator(p))
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(BGMain.isSpectator(p)) {
			event.setCancelled(true);
			return;
		}
		
		if (BGMain.GAMESTATE == GameState.PREGAME && (!p.hasPermission("bg.admin.editblocks") || !p.hasPermission("bg.admin.*"))) {
			event.setCancelled(true);
			return;
		}


		if ((p.getItemInHand().getType() == Material.COMPASS & BGMain.COMPASS)) {
			Boolean found = Boolean.valueOf(false);
			for (int i = 0; i < 300; i++) {
				List<Entity> entities = p.getNearbyEntities(i, 64.0D, i);
				for (Entity e : entities) {
					if ((!e.getType().equals(EntityType.PLAYER))|| BGMain.isSpectator((Player) e) || BGMain.isGameMaker((Player) e))
						continue;
					if(BGMain.TEAM) {
						if(BGTeam.isInTeam(p, ((Player) e).getName()))
								continue;
					}
					p.setCompassTarget(e.getLocation());
					Double distance = p.getLocation().distance(
							e.getLocation());
					DecimalFormat df = new DecimalFormat("##.#");
					BGChat.printPlayerChat(p, Translation.COMPASS_TRACK.t().replace("<player>", ((Player) e).getName()).replace("<distance>", df.format(distance)));
					found = Boolean.valueOf(true);
					break;
				}

				if (found.booleanValue()) {
					break;
				}
			}
			if (!found.booleanValue()) {
				BGChat.printPlayerChat(p, Translation.COMPASS_NOT_TRACK.t());
				p.setCompassTarget(BGMain.spawn);
			}
		}		
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerPing(ServerListPingEvent event) {
		if (BGMain.GAMESTATE != GameState.PREGAME)
			event.setMotd(ChatColor.translateAlternateColorCodes('&', BGMain.MOTD_PROGRESS_MSG));
		else
			event.setMotd(ChatColor.translateAlternateColorCodes('&', BGMain.MOTD_COUNTDOWN_MSG).replace("<time>", BGMain.TIME(BGMain.COUNTDOWN)));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBucketFill(PlayerBucketFillEvent event) {
		if(BGMain.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		if (BGMain.GAMESTATE == GameState.PREGAME)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		if(BGMain.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		if (BGMain.GAMESTATE == GameState.PREGAME)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityShootArrow(EntityShootBowEvent event) {
		if(event.getEntity() instanceof Player)
			if(BGMain.isSpectator((Player) event.getEntity())) {
				event.setCancelled(true);
				return;
			}
		if (event.getEntity() instanceof Player && BGMain.GAMESTATE == GameState.PREGAME) {
			event.getBow().setDurability((short) 0);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(BGMain.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
			event.getPlayer().getInventory().clear();
			for(int i=0;i<=8;i++) {
				event.getPlayer().getInventory().setItem(i, new ItemStack(Material.CROPS, 1));
			}
			return;
		}
		if (BGMain.GAMESTATE == GameState.PREGAME)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (BGMain.GAMESTATE != GameState.GAME) {
			event.setCancelled(true);
			return;
		}
		
		ArrayList<Block> remove = new ArrayList<Block>();
		for(Block b : event.blockList()) {
			if(BGCornucopia.isCornucopiaBlock(b)) {
				remove.add(b);
				continue;
			}
			if(BGFeast.isFeastBlock(b)) {
				remove.add(b);
				continue;
			}
		}
		event.blockList().removeAll(remove);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(BGMain.isSpectator(event.getPlayer()) || BGMain.isGameMaker(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		if (BGMain.GAMESTATE == GameState.PREGAME)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		Player p = event.getPlayer();
		
		if(BGMain.isGameMaker(p))
			BGMain.remGameMaker(p);
		if(BGMain.isSpectator(p))
			BGMain.remSpectator(p);
		
		if (BGMain.GAMESTATE != GameState.PREGAME || BGMain.ADV_CHAT_SYSTEM)
			event.setLeaveMessage(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();

		if (BGMain.GAMESTATE != GameState.PREGAME &&
				!(BGMain.SPECTATOR_SYSTEM && p.hasPermission("bg.spectator")) &&
				!(p.hasPermission("bg.admin.logingame") || p.hasPermission("bg.admin.*"))) {
			event.setKickMessage(ChatColor.RED + BGMain.GAME_IN_PROGRESS_MSG);
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, event.getKickMessage());
		} else if (event.getResult() == Result.KICK_FULL) {
			if (p.hasPermission("bg.vip.full") || p.hasPermission("bg.admin.full") 
					|| Bukkit.getServer().getMaxPlayers() > BGMain.getGamers().length) {
				event.allow();
			} else {
				event.setKickMessage(ChatColor.RED + BGMain.SERVER_FULL_MSG.replace("<players>", Integer.toString(Bukkit.getOnlinePlayers().length)));
			}
		}

		BGVanish.updateVanished();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		if (!BGMain.COMPASS.booleanValue() || !BGMain.AUTO_COMPASS.booleanValue())
			return;
		Boolean found = Boolean.valueOf(false);
		for (int i = 0; i < 300; i++) {
			List<Entity> entities = p.getNearbyEntities(i, 64.0D, i);
			for (Entity e : entities) {
				if ((e.getType().equals(EntityType.PLAYER)) && !BGMain.isSpectator((Player) e)) {
					p.setCompassTarget(e.getLocation());
					found = Boolean.valueOf(true);
					break;
				}
			}
			if (found)
				break;
		}
		if (!found.booleanValue()) {
			p.setCompassTarget(BGMain.spawn);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(event.getCause() == TeleportCause.NETHER_PORTAL || event.getCause() == TeleportCause.END_PORTAL)
			event.setCancelled(true);
		
		if(!BGMain.inBorder(event.getTo(), BorderType.STOP)) {
			event.getPlayer().teleport(event.getFrom());
			event.setCancelled(true);
		}
		
		BGVanish.updateVanished();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (BGMain.GAMESTATE == GameState.PREGAME && BGMain.ADV_CHAT_SYSTEM && event.getJoinMessage() != null) {
			BGChat.printDeathChat(ChatColor.YELLOW + event.getJoinMessage());
		}

		if (BGMain.GAMESTATE != GameState.PREGAME || BGMain.ADV_CHAT_SYSTEM) {
			event.setJoinMessage(null);
		}

		Player p = event.getPlayer();
		
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setExp(0);
		
		if (BGMain.GAMESTATE != GameState.PREGAME) {
			if (p.hasPermission("bg.admin.gamemaker") || p.hasPermission("bg.admin.*")) {
				BGMain.addGameMaker(p);
			} else if(BGMain.SPECTATOR_SYSTEM && p.hasPermission("bg.spectator")) {
				BGMain.addSpectator(p);
			}
		} else {
			if (!BGMain.ADV_CHAT_SYSTEM && !BGMain.ITEM_MENU)
				BGChat.printKitChat(p);
		}

		if(BGMain.isSpectator(p) || BGMain.isGameMaker(p)) {
			p.setPlayerListName(ChatColor.GRAY + getShortStr(p.getName()) + ChatColor.RESET);
			p.setDisplayName(ChatColor.GRAY + p.getName() + ChatColor.RESET);
		} else if (BGMain.winner(p)) {
			p.setPlayerListName(ChatColor.GOLD + getShortStr(p.getName())
					+ ChatColor.RESET);
			p.setDisplayName(ChatColor.GOLD + p.getName() + ChatColor.RESET);
		} else if (p.hasPermission("bg.admin.color")
				|| p.hasPermission("bg.admin.*")) {
			p.setPlayerListName(ChatColor.RED + getShortStr(p.getName())
					+ ChatColor.RESET);
			p.setDisplayName(ChatColor.RED + p.getName() + ChatColor.RESET);
		} else if (p.hasPermission("bg.vip.color")
				|| p.hasPermission("bg.vip.*")) {
			p.setPlayerListName(ChatColor.BLUE + getShortStr(p.getName())
					+ ChatColor.RESET);
			p.setDisplayName(ChatColor.BLUE + p.getName() + ChatColor.RESET);
		} else {
			p.setPlayerListName(p.getName());
			p.setDisplayName(p.getName());
		}
		
		if(BGMain.GAMESTATE == GameState.PREGAME) {
		List<String> pages = BGFiles.bookconf.getStringList("content");
		List<String> content = new ArrayList<String>();
		List<String> page = new ArrayList<String>();
		for(String line : pages)  {
			line = line.replace("<server_title>", BGMain.SERVER_TITLE);
			line = line.replace("<space>", ChatColor.RESET + "\n");
			line = ChatColor.translateAlternateColorCodes('&', line);
			if(!line.contains("<newpage>")) {
				page.add(line + "\n");
			} else {
				String pagestr = "";
				for(String l : page)
					pagestr = pagestr + l;
				content.add(pagestr);
				page.clear();
			}
		}
		String pagestr = "";
		for(String l : page)
			pagestr = pagestr + l;
		content.add(pagestr);	
		page.clear();
		
		ItemStack item = new ItemStack(387,1);
		
		BookMeta im = (BookMeta) item.getItemMeta();
			im.setPages(content);
			im.setAuthor(BGFiles.bookconf.getString("author"));
			im.setTitle(BGFiles.bookconf.getString("title"));
		item.setItemMeta(im);
		p.getInventory().addItem(item);
		}
		
		String playerName = p.getName();
		
		if (BGMain.SQL_USE) {
			Integer PL_ID = BGMain.getPlayerID(playerName);
			if (PL_ID == null) {
				BGMain.SQLquery("INSERT INTO `PLAYERS` (`NAME`) VALUES ('"
						+ playerName + "') ;");
			}
		}
		
		if (BGMain.REW) {
			BGReward.createUser(playerName);
		}
		
		if(p.hasPermission("bg.admin.check")) {
			BGMain.checkVersion(null, p);
		}
	}

	private String getShortStr(String s) {
		if (s.length() == 16) {
			String shorts = s.substring(0, s.length() - 4);
			return shorts;
		}
		if (s.length() == 15) {
			String shorts = s.substring(0, s.length() - 3);
			return shorts;
		}
		if (s.length() == 14) {
			String shorts = s.substring(0, s.length() - 2);
			return shorts;
		}
		if (s.length() == 13) {
			String shorts = s.substring(0, s.length() - 1);
			return shorts;
		}
		return s;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if(BGMain.isSpectator(p)) {
			event.setCancelled(true);
			return;
		}
		
		if ((BGMain.GAMESTATE == GameState.PREGAME && (!p.hasPermission("bg.admin.editblocks") || !p.hasPermission("bg.admin.*")))) {
			event.setCancelled(true);
			return;
		}

		if((BGMain.CORNUCOPIA_PROTECTED && BGCornucopia.isCornucopiaBlock(event.getBlock())) || (BGMain.FEAST_PROTECTED && BGFeast.isFeastBlock(event.getBlock()))) {
			BGChat.printPlayerChat(p, ChatColor.RED + Translation.BLOCK_DESTROY_NOT_ALLOWS.t());
			event.setCancelled(true);
			return;
		}
		
		Block b = event.getBlock();
		
		if(BGMain.DEATH_SG_PROTECTED && BGSign.signs.contains(b.getLocation())) {
				event.setCancelled(true);
				return;
		}		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		for(Block b : event.getBlocks()) {
			if(BGCornucopia.isCornucopiaBlock(b)) {
				event.setCancelled(true);
				break;
			}
			if(BGFeast.isFeastBlock(b)) {
				event.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		Block b = event.getBlock();
		if(BGCornucopia.isCornucopiaBlock(b)) {
			event.setCancelled(true);
			return;
		}
		if(BGFeast.isFeastBlock(b)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBurn(BlockBurnEvent event) {
		if((BGMain.CORNUCOPIA_PROTECTED && BGCornucopia.isCornucopiaBlock(event.getBlock())) || 
			(BGMain.FEAST_PROTECTED && BGFeast.isFeastBlock(event.getBlock()))) {
			event.setCancelled(true);
			return;
		}
	}
		
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		if(BGMain.isSpectator(p)) {
			event.setCancelled(true);
			return;
		}
		if ((BGMain.GAMESTATE == GameState.PREGAME && (!p.hasPermission("bg.admin.editblocks") || !p.hasPermission("bg.admin.*")))) {
			event.setCancelled(true);
			return;
		}
		
		if((BGMain.CORNUCOPIA_PROTECTED && BGCornucopia.isCornucopiaBlock(event.getBlock())) || (BGMain.FEAST_PROTECTED && BGFeast.isFeastBlock(event.getBlock()))) {
			BGChat.printPlayerChat(p, ChatColor.RED + Translation.BLOCK_DESTROY_NOT_ALLOWS.t());
			event.setCancelled(true);
			return;
		}
	}
		
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		if(BGMain.isSpectator(event.getPlayer())) {
			if(BGMain.ADV_CHAT_SYSTEM) {
				BGChat.printPlayerChat(event.getPlayer(), ChatColor.RED + Translation.SPECTATORS_CHAT_NOT_ALLOWS.t());
				event.setCancelled(true);			
				return;
			} else {
				event.getRecipients().clear();
				event.getRecipients().addAll(BGMain.getSpectators());
				event.getRecipients().addAll(BGMain.getOnlineOps());
				event.setFormat(ChatColor.ITALIC + "[SPECTATOR] " + ChatColor.RESET + event.getFormat());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		
		if(BGMain.isGameMaker(p)) {
			event.setQuitMessage(null);
			BGMain.remGameMaker(p);
			return;
		}
		
		if (BGMain.isSpectator(p)) {
			event.setQuitMessage(null);
			BGMain.remSpectator(p);
			return;
		}
		
		if (BGMain.GAMESTATE == GameState.PREGAME && BGMain.ADV_CHAT_SYSTEM && event.getQuitMessage() != null) {
			BGChat.printDeathChat(ChatColor.YELLOW + event.getQuitMessage());
		}

		if (BGMain.GAMESTATE != GameState.PREGAME || BGMain.ADV_CHAT_SYSTEM) {
			event.setQuitMessage(null);
		}

		if (BGMain.GAMESTATE == GameState.GAME & !p.isDead()) {
			BGChat.printDeathChat(p.getName() + " left the game.");
			if (!BGMain.ADV_CHAT_SYSTEM) {
				BGChat.printDeathChat(BGMain.getGamers().length - 1 + " players remaining.");
				BGChat.printDeathChat("");
			}
			Location light = p.getLocation();
			last_quit = p.getName();
			p.setHealth(0);
			Bukkit.getServer().getWorlds().get(0)
					.strikeLightningEffect(light.add(0.0D, 100.0D, 0.0D));
		}

		if (BGMain.NEW_WINNER != p.getName() && BGMain.GAMESTATE != GameState.PREGAME) {
			Bukkit.getServer().getScheduler()
					.scheduleSyncDelayedTask(BGMain.instance, new Runnable() {
						public void run() {
							BGMain.checkwinner();

							if (BGMain.SQL_USE) {
								Integer PL_ID = BGMain.getPlayerID(last_quit);
								if (last_quit == BGMain.NEW_WINNER) {
									
								} else {
									BGMain.SQLquery("UPDATE `PLAYS` SET deathtime = NOW(), `DEATH_REASON` = 'QUIT' WHERE `REF_PLAYER` = "
											+ PL_ID
											+ " AND `REF_GAME` = "
											+ BGMain.SQL_GAMEID + " ;");
								}
							}
						}
					}, 60L);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entityDamager = event.getDamager();
	    Entity entityDamaged = event.getEntity();
	   
	    if(entityDamager instanceof Arrow) {
	        if(entityDamaged instanceof Player && ((Arrow) entityDamager).getShooter() instanceof Player) {
	            Arrow arrow = (Arrow) entityDamager;
	 
	            Vector velocity = arrow.getVelocity();
	 
	            Player shooter = (Player) arrow.getShooter();
	            Player damaged = (Player) entityDamaged;
	 
	            if(BGMain.isSpectator(damaged) || BGMain.isGameMaker(damaged)) {
	                damaged.teleport(BGMain.getSpawn());
	                BGChat.printPlayerChat(damaged, ChatColor.RED + Translation.SPECTATOR_IN_THE_WAY.t());
	               
	                Arrow newArrow = shooter.launchProjectile(Arrow.class);
	                newArrow.setShooter(shooter);
	                newArrow.setVelocity(velocity);
	                newArrow.setBounce(false);
	               
	                event.setCancelled(true);
	                arrow.remove();
	            }
	        }
	    } else if(entityDamager instanceof Player) {
			if(BGMain.isSpectator((Player) entityDamager)) {
				event.setCancelled(true);
				return;
			}
	    }
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(event.getRightClicked() instanceof Player && BGMain.isSpectator((Player) event.getRightClicked())) {
			if(!BGMain.isSpectator(event.getPlayer()) && !BGMain.isGameMaker(event.getPlayer())) {
				event.getRightClicked().teleport(BGMain.getSpawn());
				BGChat.printPlayerChat((Player) event.getRightClicked(), ChatColor.RED + Translation.SPECTATOR_IN_THE_WAY.t());
				
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			if(BGMain.isSpectator((Player) event.getEntity()) || BGMain.isGameMaker((Player) event.getEntity())) {
				event.setCancelled(true);
				return;
			}
		}
		
		if (BGMain.GAMESTATE != GameState.GAME && event.getEntity() instanceof Player) {
			event.setCancelled(true);
			return;
		}

		if (BGMain.GAMESTATE == GameState.PREGAME && !(event.getEntity() instanceof Player)) {
			event.setCancelled(true);
			return;
		}
	}

	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player dp = event.getEntity();
		
		if (BGMain.isSpectator(dp) || BGMain.isGameMaker(dp)) {
			event.setDeathMessage(null);
			event.getDrops().clear();
			event.setDroppedExp(0);
			return;
		}
				
		if (BGMain.DEATH_SIGNS) {
			Location loc = dp.getLocation();
			String fl = BGFiles.dsign.getString("FIRST_LINE");
			String sl = BGFiles.dsign.getString("SECOND_LINE");
			String tl = BGFiles.dsign.getString("THIRD_LINE");
			String fol = BGFiles.dsign.getString("FOURTH_LINE");
			
			if(fl != null)	
				fl = fl.replace("[name]", dp.getName());
			if(sl != null)
				sl = sl.replace("[name]", dp.getName());
			if(tl != null)
				tl = tl.replace("[name]", dp.getName());
			
			if(fol != null)
				fol = fol.replace("[name]", dp.getName());
			
			BGSign.createSign(loc, fl, sl, tl, fol);
		}
		
		
		if(dp.getKiller() != null && dp.getKiller() instanceof Player) {
			Player killer = dp.getKiller();
			if(BGKit.hasAbility(killer, 14)) {
				if(killer.getFoodLevel() <= 14) {
					killer.setFoodLevel(killer.getFoodLevel()+ 6);
				}else {
					killer.setFoodLevel(20);
				}
			}
			if(BGMain.REW && last_headshot != dp.getName() && BGMain.COINS_FOR_KILL != 0){
				BGReward.giveCoins(killer.getName(), BGMain.COINS_FOR_KILL);
				if(BGMain.COINS_FOR_KILL == 1)
					BGChat.printPlayerChat(killer, "You got 1 Coin for killing "+dp.getName());
				else
					BGChat.printPlayerChat(killer, "You got "+BGMain.COINS_FOR_KILL+" Coins for killing "+dp.getName());
			}
		}

		if (last_quit == event.getEntity().getName() || last_headshot == event.getEntity().getName()) {
			event.setDeathMessage(null);
			return;
		}

		if (BGMain.GAMESTATE != GameState.PREGAME) {
			Player p = event.getEntity();

			if (BGMain.SQL_USE) {
				Integer PL_ID = BGMain.getPlayerID(p.getName());

				Integer KL_ID = null;
				if (p.getKiller() != null) {
					KL_ID = BGMain.getPlayerID(p.getKiller().getName());
				} else {
					KL_ID = null;
				}
				
				String cause = null;
				try{
					cause = p.getLastDamageCause().getCause().name().toString();
				}catch (NullPointerException e) {
					
				}

				BGMain.SQLquery("UPDATE `PLAYS` SET deathtime = NOW(), `REF_KILLER` = "
						+ KL_ID
						+ ", `DEATH_REASON` = '"
						+ cause
						+ "' WHERE `REF_PLAYER` = "
						+ PL_ID
						+ " AND `REF_GAME` = " + BGMain.SQL_GAMEID + " ;");
			}
			
			Location light = p.getLocation();
			p.kickPlayer(ChatColor.RED + event.getDeathMessage() + ".");
			
			BGChat.printDeathChat(event.getDeathMessage() + ".");
			if (!BGMain.ADV_CHAT_SYSTEM) {
				BGChat.printDeathChat(BGMain.getGamers().length + " players remaining.");
				BGChat.printDeathChat("");
			}
			Bukkit.getServer().getWorlds().get(0).strikeLightningEffect(light.add(0, 100, 0));
		}

		event.setDeathMessage(null);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(event.getMessage().toLowerCase().startsWith("/me ")) {
			event.setCancelled(true);
			return;
		}
			
		if(event.getMessage().toLowerCase().startsWith("/say ")) {
			if(event.getPlayer().hasPermission("bg.admin.*")) {
				String say = event.getMessage().substring(5);
				BGChat.printInfoChat(say);
			}
			event.setCancelled(true);
			return;
		}
	}
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if ((event.getEntity() instanceof Player) && BGMain.isSpectator((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Entity entity = event.getAttacker();
        if (entity instanceof Player && BGMain.isSpectator((Player) entity)) {
        	event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entity = event.getEntered();
        if (entity instanceof Player && BGMain.isSpectator((Player) entity)) {
        	event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDamage(VehicleDamageEvent event) {
        Entity entity = event.getAttacker();
        if (entity instanceof Player && BGMain.isSpectator((Player) entity)) {
        	event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEntityShear(PlayerShearEntityEvent event) {
        if (BGMain.isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event) {
    	if(event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
    		if(BGMain.isSpectator((Player) event.getEntity().getShooter())) {
    			event.getEntity().remove();
    			return;
    		}
    	}
    }
}
