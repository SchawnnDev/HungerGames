package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import commands.*;

import threads.*;
import timers.*;
import utilities.*;
import events.*;
import utilities.enums.*;

public class BGMain extends JavaPlugin {		
	public static  ReentrantLock lock = new ReentrantLock(true);

	public static GameState GAMESTATE = GameState.PREGAME;
	public static Language LANGUAGE = Language.ENGLISH;
	public static String HELP_MESSAGE = null;
	public static String SERVER_FULL_MSG = "";
	public static String WORLD_BORDER_MSG = "";
	public static String GAME_IN_PROGRESS_MSG = "";
	public static String KIT_BUY_WEB = "";
	public static String NEW_WINNER = "";
	public static String MOTD_PROGRESS_MSG = "";
	public static String MOTD_COUNTDOWN_MSG = "";
	public static String NO_KIT_MSG = "";
	public static String SERVER_TITLE = null;
	public static Boolean ADV_CHAT_SYSTEM = true;
	public static Boolean KIT_PREFIX = true;
	public static int COUNTDOWN_SECONDS = 300;
	public static int FINAL_COUNTDOWN_SECONDS = 60;
	public static int MAX_GAME_RUNNING_TIME = 60;
	public static int MINIMUM_PLAYERS = 4;
	public static int GAME_ENDING_TIME = 50;
	public static final int WINNER_PLAYERS = 1;
	public static boolean REGEN_WORLD = false;
	public static boolean RANDOM_START = false;
	public static boolean SHOW_TIPS = true;
	public static boolean COMPASS = true;
	public static boolean AUTO_COMPASS = false;
	public static boolean ADV_ABI = false;
	public static boolean SIMP_REW = false;
	public static boolean REW = false;
	public static boolean DEATH_SIGNS = true;
	public static boolean DEATH_SG_PROTECTED = true;
	public static boolean DEFAULT_KIT = false;
	public static boolean CORNUCOPIA = true;
	public static boolean CORNUCOPIA_CHESTS = false;
	public static boolean TEAM = true;
	public static boolean GEN_MAPS = false;
	public static boolean ITEM_MENU = true;
	public static boolean CORNUCOPIA_ITEMS = true;
	public static boolean CORNUCOPIA_PROTECTED = true;
	public static boolean FEAST = true;
	public static boolean FEAST_CHESTS = false;
	public static boolean FEAST_PROTECTED = true;
	public static boolean SPECTATOR_SYSTEM = false;
	public static boolean SQL_DSC = false;
	public static boolean PLAYERS_VISIBLE = false;
	public static Location spawn;
	public static String LAST_WINNER = "";

    @Getter
	private static ArrayList<Player> spectators = new ArrayList<>();

    @Getter
	private static ArrayList<Player> gamemakers = new ArrayList<>();

	public static int COUNTDOWN = 0;
	public static int FINAL_COUNTDOWN = 0;
	public static int GAME_RUNNING_TIME = 0;
	public static int WORLDRADIUS = 250;
	public static boolean SQL_USE = false;
	public static int FEAST_SPAWN_TIME = 30;
	public static int COINS_FOR_KILL = 1;
	public static int COINS_FOR_WIN = 5;
	
	public static int SQL_GAMEID = 0;
	public static String SQL_HOST = null;
	public static String SQL_PORT = null;
	public static String SQL_USER = null;
	public static String SQL_PASS = null;
	public static String SQL_DATA = null;
	public static Connection con = null;

    @Getter
	public static BGMain instance;
    @Getter
	private static Logger log;
	
	public void onLoad() {
		instance = this;
		log = getLogger();
		try {
			new BGFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("Deleting old world.");
		Bukkit.getServer().unloadWorld("world", false);
		deleteDir(new File("world"));

		Random r = new Random();
		
		REGEN_WORLD = getConfig().getBoolean("REGEN_WORLD");
		GEN_MAPS = BGFiles.worldconf.getBoolean("GEN_MAPS");
		
		if (!REGEN_WORLD && !(GEN_MAPS && r.nextBoolean())) {
			List<String> mapnames = BGFiles.worldconf.getStringList("WORLDS");
			
			String map = mapnames.get(r.nextInt(mapnames.size()));
			String[] splitmap = map.split(",");
			
			log.info("Copying saved world. ("+splitmap[0]+")");
			try {
				copyDirectory(new File(getDataFolder(), splitmap[0]),
						new File("world"));
			} catch (IOException e) {
				log.warning("Error: " + e.toString());
			}
			if(splitmap.length == 2)
				BGMain.WORLDRADIUS = Integer.valueOf(Integer.parseInt(splitmap[1]));
			else
				BGMain.WORLDRADIUS = 300;
		} else {
			log.info("Generating new world.");
			BGMain.WORLDRADIUS = Integer.valueOf(getConfig().getInt("WORLD_BORDER_RADIUS"));
		}
	}

	private void registerEvents() {
		BGGameListener gl = new BGGameListener();
		BGAbilitiesListener al = new BGAbilitiesListener();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(gl, this);
		pm.registerEvents(al, this);
	}
		
	public void registerCommands() {
		ConsoleCommandSender console = Bukkit.getConsoleSender();

		if (getCommand("help") != null) 
			getCommand("help").setExecutor(new BGPlayer()); 
		else 
			console.sendMessage(ChatColor.RED+"getCommand help returns null");

		if (getCommand("kit") != null) 
			getCommand("kit").setExecutor(new BGPlayer()); 
		else 
			console.sendMessage(ChatColor.RED+"getCommand kit returns null");

		if (getCommand("kitinfo") != null) 
			getCommand("kitinfo").setExecutor(new BGPlayer()); 
		else 
			console.sendMessage(ChatColor.RED+"getCommand kitinfo returns null");

		if (getCommand("start") != null) 
			getCommand("start").setExecutor(new BGConsole()); 
		else 
			console.sendMessage(ChatColor.RED+"getCommand start returns null");

		if (getCommand("spawn") != null) 
			getCommand("spawn").setExecutor(new BGPlayer()); 
		else 
			console.sendMessage(ChatColor.RED+"getCommand spawn returns null");
		
		if (getCommand("coin") != null)
			getCommand("coin").setExecutor(new BGConsole());
		else
			console.sendMessage(ChatColor.RED+"getCommand coin returns null");
		if(getCommand("team") != null)
			getCommand("team").setExecutor(new BGPlayer());
		else
			console.sendMessage(ChatColor.RED+"getCommand team returns null");
		if(getCommand("teleport") != null)
			getCommand("teleport").setExecutor(new BGPlayer());
		else
			console.sendMessage(ChatColor.RED+"getCommand teleport returns null");
	}
	
	public void onEnable() {
		instance = this;
		Bukkit.getServer().getWorlds().get(0).setDifficulty(Difficulty.PEACEFUL);
		
		ADV_ABI = getConfig().getBoolean("ADVANCED_ABILITIES");
		
		registerEvents();
		registerCommands();
		new BGKit();
		new BGChat();
		
		log.info("Loading configuration options.");
		DEATH_SIGNS = getConfig().getBoolean("DEATH_SIGNS");
		DEATH_SG_PROTECTED = BGFiles.dsign.getBoolean("PROTECTED");
		KIT_BUY_WEB = getConfig().getString("MESSAGE.KIT_BUY_WEBSITE");
		SERVER_TITLE = getConfig().getString("MESSAGE.SERVER_TITLE");
		HELP_MESSAGE = getConfig().getString("MESSAGE.HELP_MESSAGE");
		RANDOM_START = getConfig().getBoolean("RANDOM_START");
		DEFAULT_KIT = getConfig().getBoolean("DEFAULT_KIT");
		SHOW_TIPS = getConfig().getBoolean("SHOW_TIPS");
		REGEN_WORLD = getConfig().getBoolean("REGEN_WORLD");
		CORNUCOPIA = getConfig().getBoolean("CORNUCOPIA");
		CORNUCOPIA_ITEMS = BGFiles.cornconf.getBoolean("ITEM_SPAWN");
		CORNUCOPIA_CHESTS = BGFiles.cornconf.getBoolean("CHESTS");
		CORNUCOPIA_PROTECTED = BGFiles.cornconf.getBoolean("PROTECTED");
		FEAST = getConfig().getBoolean("FEAST");
		FEAST_CHESTS = BGFiles.feastconf.getBoolean("CHESTS");
		FEAST_PROTECTED = BGFiles.feastconf.getBoolean("PROTECTED");
		SPECTATOR_SYSTEM = getConfig().getBoolean("SPECTATOR_SYSTEM");
		TEAM = getConfig().getBoolean("TEAM");
		NO_KIT_MSG = getConfig().getString("MESSAGE.NO_KIT_PERMISSION");
		GAME_IN_PROGRESS_MSG = getConfig().getString("MESSAGE.GAME_PROGRESS");
		SERVER_FULL_MSG = getConfig().getString("MESSAGE.SERVER_FULL");
		WORLD_BORDER_MSG = getConfig().getString("MESSAGE.WORLD_BORDER");
		MOTD_PROGRESS_MSG = getConfig().getString("MESSAGE.MOTD_PROGRESS");
		MOTD_COUNTDOWN_MSG = getConfig().getString("MESSAGE.MOTD_COUNTDOWN");
		ADV_CHAT_SYSTEM = getConfig().getBoolean("ADVANCED_CHAT");
		KIT_PREFIX = getConfig().getBoolean("KIT_PREFIX");
		SQL_USE = getConfig().getBoolean("MYSQL");
		SQL_HOST = getConfig().getString("HOST");
		SQL_PORT = getConfig().getString("PORT");
		SQL_USER = getConfig().getString("USERNAME");
		SQL_PASS = getConfig().getString("PASSWORD");
		SQL_DATA = getConfig().getString("DATABASE");
		SIMP_REW = getConfig().getBoolean("SIMPLE_REWARD");
		REW = getConfig().getBoolean("REWARD");
		COINS_FOR_KILL = getConfig().getInt("COINS_FOR_KILL");
		COINS_FOR_WIN = getConfig().getInt("COINS_FOR_WIN");
		MINIMUM_PLAYERS = getConfig().getInt("MINIMUM_PLAYERS_START");
		MAX_GAME_RUNNING_TIME = getConfig().getInt("TIME.MAX_GAME-MIN");
		COUNTDOWN_SECONDS = getConfig().getInt("TIME.COUNTDOWN-SEC");
		GAME_ENDING_TIME = getConfig().getInt("TIME.GAME_ENDING-MIN");
		FINAL_COUNTDOWN_SECONDS = getConfig().getInt("TIME.FINAL_COUNTDOWN-SEC");
		COMPASS = getConfig().getBoolean("COMPASS");
		AUTO_COMPASS = getConfig().getBoolean("AUTO_COMPASS");
		ITEM_MENU = getConfig().getBoolean("ITEM_MENU");
		PLAYERS_VISIBLE = getConfig().getBoolean("PLAYERS_VISIBLE");
				
		String lang = getConfig().getString("LANGUAGE");
		if(lang == "en")
			LANGUAGE = Language.ENGLISH;
		else if(lang == "de")
			LANGUAGE = Language.GERMAN;
		
		log.info("Setting language to " + BGMain.LANGUAGE.toString().toLowerCase() + "...");
		if(BGMain.LANGUAGE == Language.ENGLISH)
			BGMain.copy(BGMain.instance.getResource("en.yml"), new File(BGMain.instance.getDataFolder(), "lang.yml"));
		else if(BGMain.LANGUAGE == Language.GERMAN)
			BGMain.copy(BGMain.instance.getResource("de.yml"), new File(BGMain.instance.getDataFolder(), "lang.yml"));
		else
			BGMain.copy(BGMain.instance.getResource("en.yml"), new File(BGMain.instance.getDataFolder(), "lang.yml"));
		Translation.e = YamlConfiguration.loadConfiguration(new File(BGMain.instance.getDataFolder(), "lang.yml"));
		
		if(REW && !SQL_USE) {
			log.warning("MySQL has to be enabled for advanced reward, turning it off.");
			REW = false;
		}
		
		if(FEAST) {
			FEAST_SPAWN_TIME = Integer.valueOf(BGFiles.feastconf.getInt("SPAWN_TIME"));
		}
		
		if(CORNUCOPIA) {
			BGCornucopia.createCorn();
		}
		
		if(BGMain.WORLDRADIUS < 60) {
			log.warning("Worldborder radius has to be 60 or higher!");
			getServer().getPluginManager().disablePlugin(this);
		}

		log.info("Getting winner of last game.");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(getDataFolder(),"leaderboard.yml")));
		} catch (FileNotFoundException e) {
			log.warning(e.toString());
		}

		String line = null;
		String merke = null;
		try {
			while ((line = br.readLine()) != null)
				merke = line;
		} catch (IOException e) {
			log.warning(e.toString());
		}
		try {
			br.close();
		} catch (IOException e) {
			log.warning(e.toString());
		}

		LAST_WINNER = merke;


		spawn = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();

		COUNTDOWN = COUNTDOWN_SECONDS;
		FINAL_COUNTDOWN = FINAL_COUNTDOWN_SECONDS;
		GAME_RUNNING_TIME = Integer.valueOf(0);

		BGMain.GAMESTATE = GameState.PREGAME;

		if (SQL_USE) {
			SQLconnect();
			SQLquery("CREATE TABLE IF NOT EXISTS `GAMES` (`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, `STARTTIME` datetime NOT NULL, `ENDTIME` datetime, `REF_WINNER` int(10), PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1 ;");
			SQLquery("CREATE TABLE IF NOT EXISTS `PLAYERS` (`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, `NAME` varchar(255) NOT NULL, PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1 ;");
			SQLquery("CREATE TABLE IF NOT EXISTS `PLAYS` (`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, `REF_PLAYER` int(10), `REF_GAME` int(10), `KIT` varchar(255), `DEATHTIME` datetime, `REF_KILLER` int(10), `DEATH_REASON` varchar(255), PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1 ;");
			SQLquery("CREATE TABLE IF NOT EXISTS `REWARD` (`ID` int(10) unsigned NOT NULL AUTO_INCREMENT, `REF_PLAYER` int(10) NOT NULL, `COINS` int(10) NOT NULL, PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=UTF8 AUTO_INCREMENT=1 ;");
		}

		Location loc = randomLocation(spawn.getChunk()).add(0.0D, 30.0D,0.0D);
		Bukkit.getServer().getWorlds().get(0).loadChunk(loc.getChunk());
		new PreGameTimer();

		PluginDescriptionFile pdfFile = getDescription();
		log.info("Plugin enabled");
		log.info("Author: " + pdfFile.getAuthors() + " | Version: " + pdfFile.getVersion());
		log.info("All rights reserved. This plugin is free to download. If you had to pay for it, contact us immediately!");
		
		log.info("Game phase: 1 - Waiting");
	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		Bukkit.getServer().getScheduler().cancelAllTasks();
				
		if (SQL_USE) {
			if (SQL_GAMEID != 0) {
				Integer PL_ID = getPlayerID(NEW_WINNER);
				SQLquery("UPDATE `GAMES` SET `ENDTIME` = NOW(), `REF_WINNER` = "
						+ PL_ID + " WHERE `ID` = " + SQL_GAMEID + " ;");
				SQL_DSC = true;
				SQLdisconnect();
			}
		}

		for (Player p : getPlayers()) {
			p.kickPlayer(ChatColor.YELLOW + "Server is restarting!");
		}
		
		Bukkit.getServer().unloadWorld(Bukkit.getServer().getWorlds().get(0), false);
		
		new File(BGMain.instance.getDataFolder(), "lang.yml").delete();
		
		log.info("Plugin disabled");
		log.info("Author: " + pdfFile.getAuthors() + " | Version: " + pdfFile.getVersion());
		
		Bukkit.getServer().shutdown();
	}

	public static void startgame() {
		log.info("Game phase: 2 - Starting");
		PreGameTimer.cancel();
		new InvincibilityTimer();

		BGMain.GAMESTATE = GameState.INVINCIBILITY;
		if(CORNUCOPIA_ITEMS && CORNUCOPIA)
			BGCornucopia.spawnItems();
		
		if (SQL_USE) {
			PreparedStatement statement = null;
			ResultSet generatedKeys = null;

			try {
				statement = con.prepareStatement(
						"INSERT INTO `GAMES` (`STARTTIME`) VALUES (NOW()) ;",
						Statement.RETURN_GENERATED_KEYS);

				int affectedRows = statement.executeUpdate();
				if (affectedRows == 0) {
					log.warning("[BukkitGames] Couldn't get GameID!");
				}

				generatedKeys = statement.getGeneratedKeys();
				if (generatedKeys.next()) {
					SQL_GAMEID = (int) generatedKeys.getLong(1);
				} else {
					log.warning("[BukkitGames] Couldn't get GameID!");
				}
			} catch (Exception e) {
				log.warning(e.getMessage());
			} finally {
				if (generatedKeys != null)
					try {
						generatedKeys.close();
					} catch (Exception e) {}
				
				if (statement != null)
					try {
						statement.close();
					} catch (SQLException logOrIgnore) {}
			}

		}

		Bukkit.getServer().getWorlds().get(0).loadChunk(getSpawn().getChunk());
		Bukkit.getWorlds().get(0).setDifficulty(Difficulty.HARD);
		for (Player p : getPlayers()) {
			if(isGameMaker(p) || isSpectator(p))
				continue;
			if(p.isInsideVehicle())
				p.getVehicle().eject();
			if (!RANDOM_START) {
				Random r = new Random();
				Location startFrom = getSpawn();
				Location loc = startFrom.clone();
				int addx;
				int addy;
				do {
					
					addx = (r.nextBoolean() ? 1 : -1) * r.nextInt(7);
					addy = (r.nextBoolean() ? 1 : -1) * r.nextInt(7);
				}while((Math.abs(addx)+Math.abs(addy)) < 5);
				loc.add(addx, 60, addy);
				loc.setY(Bukkit.getServer().getWorlds().get(0).getHighestBlockYAt(loc) + 1.5);
				p.teleport(loc);
			} else {
				Location tploc = getRandomLocation();
				tploc.setY(Bukkit.getServer().getWorlds().get(0).getHighestBlockYAt(tploc) + 1.5);
				p.teleport(tploc);
			}
			p.setHealth(20);
			p.setFoodLevel(20);
			p.setExhaustion(20);
			p.setFlying(false);
			p.getEnderChest().clear();
			p.setGameMode(GameMode.SURVIVAL);
			p.setFireTicks(0);
			p.setAllowFlight(false);
			for(PotionEffect e : p.getActivePotionEffects())
				p.removePotionEffect(e.getType());
			
			if(p.getOpenInventory() != null)
				p.getOpenInventory().close();
			
			BGKit.giveKit(p);
			if (SQL_USE & !BGMain.isSpectator(p)) {
				Integer PL_ID = getPlayerID(p.getName());
				SQLquery("INSERT INTO `PLAYS` (`REF_PLAYER`, `REF_GAME`, `KIT`) VALUES ("
						+ PL_ID
						+ ","
						+ SQL_GAMEID
						+ ",'"
						+ BGKit.getKit(p)
						+ "') ;");
			}
		}

		Bukkit.getServer().getWorlds().get(0).setTime(0L);
		Bukkit.getServer().getWorlds().get(0).setStorm(false);
		Bukkit.getServer().getWorlds().get(0).setThundering(false);
		if (ADV_CHAT_SYSTEM) {
			BGChat.printInfoChat(" --- " + Translation.GAMES_HAVE_BEGUN.t() + " ---");
			BGChat.printDeathChat(ChatColor.YELLOW + Translation.MAY_ODDS_BE_IN_YOUR_FAVOR.t());
		} else {
			BGChat.printTimeChat("");
			BGChat.printTimeChat(Translation.GAMES_HAVE_BEGUN.t());
		}
		BGChat.printTimeChat(Translation.INVINCIBLE_FOR.t().replace("<time>", TIME(FINAL_COUNTDOWN_SECONDS)));
	}
	
	private void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

	public static void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public static Location getSpawn() {
		Location loc = Bukkit.getWorlds().get(0).getSpawnLocation();
		loc.setY(Bukkit.getWorlds().get(0).getHighestBlockYAt(Bukkit.getWorlds().get(0).getSpawnLocation()) + 1.5);
		return loc;
	}

	public static Player[] getGamers() {
		ArrayList<Player> gamers = new ArrayList<>();

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!BGMain.isSpectator(p) && !BGMain.isGameMaker(p)) {
				gamers.add(p);
			}
		}
		return (Player[]) gamers.toArray(new Player[0]);
	}

	public static Player[] getPlayers() {
        return (Player[]) Bukkit.getOnlinePlayers().toArray();
	}

	public static Location randomLocation(Chunk c) {
		Random random = new Random();
		Location startFrom = Bukkit.getWorlds().get(0).getSpawnLocation();
		Location loc = startFrom.clone();
		loc.add((random.nextBoolean() ? 1 : -1) * random.nextInt(WORLDRADIUS),
				60,
				(random.nextBoolean() ? 1 : -1) * random.nextInt(WORLDRADIUS));
		int newY = Bukkit.getWorlds().get(0).getHighestBlockYAt(loc);
		loc.setY(newY);
		return loc;
	}

	public static Location getRandomLocation() {
		Random random = new Random();
		Location startFrom = Bukkit.getWorlds().get(0).getSpawnLocation();
		Location loc;
		do{
			loc = startFrom.clone();
			loc.add((random.nextBoolean() ? 1 : -1) * random.nextInt(WORLDRADIUS),
				60,
				(random.nextBoolean() ? 1 : -1) * random.nextInt(WORLDRADIUS));
			int newY = Bukkit.getWorlds().get(0).getHighestBlockYAt(loc);
			loc.setY(newY);
		} while(!BGMain.inBorder(loc));
		return loc;
	}

    public static boolean inBorder(Location loc){
        // Actually useless
        return false;
    }

	public static void checkwinner() {
		if (getGamers().length <= WINNER_PLAYERS)
			if (getGamers().length == 0) {
				GameTimer.cancel();
				Bukkit.getServer().shutdown();
			} else {
				GameTimer.cancel();
				String winnername = getGamers()[0].getName();
				NEW_WINNER = winnername;
				log.info("GAME ENDED! Winner: " + winnername);
				try {
					String contents = winnername;
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							new File(instance.getDataFolder(), "leaderboard.yml"), true));
					writer.newLine();
					writer.write(contents);
					writer.flush();
					writer.close();
				} catch (Exception ex) {
					log.warning(ex.toString());
				}
				
				final Player pl = getGamers()[0];
				pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1.0F, (byte) 1);
				pl.setGameMode(GameMode.CREATIVE);
				
				if(SQL_USE) {
					Integer PL_ID = getPlayerID(winnername);
					SQLquery("UPDATE `PLAYS` SET deathtime = NOW(), `DEATH_REASON` = 'WINNER' WHERE `REF_PLAYER` = "
							+ PL_ID
							+ " AND `REF_GAME` = "
							+ SQL_GAMEID + " ;");
				}
				
				if(REW) {
					if (getPlayerID(winnername) == 0) {
						BGReward.createUser(winnername);
						BGReward.giveCoins(winnername, BGMain.COINS_FOR_WIN);
					} else {
						BGReward.giveCoins(winnername, BGMain.COINS_FOR_WIN);
					}
				}
				final boolean R = REW;
				final int CFW = COINS_FOR_WIN;
				String text = "";
				
				if(R && CFW != 0) {
					text = "You got ";
					if(CFW == 1)
						text += "1 coin for winning the game!";
					else
						text += CFW+" coins for winning the game!";
				} 
				BGChat.printPlayerChat(pl, ChatColor.GOLD + "" + ChatColor.BOLD + "YOU HAVE WON THIS GAME!" + text);
				
				Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BGMain.instance, new Runnable() {
					
					public void run() {
						Random r = new Random();
						if(!pl.isOnline()) {
							Bukkit.getServer().getScheduler().cancelAllTasks();
							Bukkit.getServer().shutdown();
						}
						spawnRandomFirework(Bukkit.getServer().getWorlds().get(0).getHighestBlockAt(pl.getLocation().add(0, 0, r.nextInt(5) + 5).add(0, 5, 0)).getLocation());
						spawnRandomFirework(Bukkit.getServer().getWorlds().get(0).getHighestBlockAt(pl.getLocation().add(r.nextInt(5) + 5, 0, 0).add(0, 5, 0)).getLocation());
						spawnRandomFirework(Bukkit.getServer().getWorlds().get(0).getHighestBlockAt(pl.getLocation().add(r.nextInt(5) + 5, 0, r.nextInt(5) + 5).add(0, 5, 0)).getLocation());
						spawnRandomFirework(Bukkit.getServer().getWorlds().get(0).getHighestBlockAt(pl.getLocation().add(-r.nextInt(5) - 5, 0, 0).add(0, 5, 0)).getLocation());
						spawnRandomFirework(Bukkit.getServer().getWorlds().get(0).getHighestBlockAt(pl.getLocation().add(0, 0, -r.nextInt(5) - 5).add(0, 5, 0)).getLocation());
						spawnRandomFirework(Bukkit.getServer().getWorlds().get(0).getHighestBlockAt(pl.getLocation().add(-r.nextInt(5) - 5, 0, -r.nextInt(5) - 5).add(0, 5, 0)).getLocation());
						spawnRandomFirework(Bukkit.getServer().getWorlds().get(0).getHighestBlockAt(pl.getLocation().add(-r.nextInt(5) - 5, 0, r.nextInt(5) + 5).add(0, 5, 0)).getLocation());
						spawnRandomFirework(Bukkit.getServer().getWorlds().get(0).getHighestBlockAt(pl.getLocation().add(r.nextInt(5) + 5, 0, -r.nextInt(5) - 5).add(0, 5, 0)).getLocation());
					}
					
				}, 0, 20);
				
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BGMain.instance, new Runnable() {
					
					public void run() {
						if(pl.isOnline())
							pl.kickPlayer(ChatColor.GOLD + "" + ChatColor.BOLD + "YOU HAVE WON THIS GAME! \n" + ChatColor.GOLD +"Thanks for playing the BukkitGames!");
						
						Bukkit.getServer().getScheduler().cancelAllTasks();
						Bukkit.getServer().shutdown();
					}
					
				}, 20*10);
			}
	}

	public static void spawnRandomFirework(Location loc) {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        Random r = new Random();   

        int rt = r.nextInt(4) + 1;
        Type type = Type.BALL;       
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;
        
        Color c1 = Color.RED;
        Color c2 = Color.YELLOW;
        Color c3 = Color.ORANGE;
       
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withColor(c2).withFade(c3).with(type).trail(r.nextBoolean()).build();
        fwm.addEffect(effect);
       
        int rp = r.nextInt(2) + 1;
        fwm.setPower(rp);
        
        fw.setFireworkMeta(fwm);           
	}
	
	public static void deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				deleteDir(new File(dir, children[i]));
			}
		}
		dir.delete();
	}

	public static String TIME(int i) {
		if (i >= 60) {
			int time = i / 60;
			String add = "";
			if (time > 1) {
				add = "s";
			}
			return time + " minute" + add;
		}
        int time = i;
		String add = "";
		if (time > 1) {
			add = "s";
		}
		return time + " seconde" + add;
	}

	public static boolean winner(Player p) {
		if (LAST_WINNER == null) {
			return false;
		}
		if (LAST_WINNER.equals(p.getName())) {
			return true;
		} else {
			return false;
		}
	}

	public static int getPlayerID(String playername) {
		try {
			Statement stmt = con.createStatement();
			ResultSet r = stmt
					.executeQuery("SELECT `ID`, `NAME` FROM `PLAYERS` WHERE `NAME` = '"
							+ playername + "' ;");
			r.last();
			if (r.getRow() == 0) {
				stmt.close();
				r.close();
				return 0;
			}
			int PL_ID = r.getInt("ID");
			stmt.close();
			r.close();
			return PL_ID;
		} catch (SQLException ex) {
			System.err.println("Error with following query: "
					+ "SELECT `ID`, `NAME` FROM `PLAYERS` WHERE `NAME` = '"
					+ playername + "' ;");
			System.err.println("MySQL-Error: " + ex.getMessage());
			return 0;
		} catch (NullPointerException ex) {
			System.err
					.println("Error while performing a query. (NullPointerException)");
			return 0;
		}
	}

	public static void SQLconnect() {
		try {
			log.info("Connecting to MySQL database...");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String conn = "jdbc:mysql://" + SQL_HOST + ":" + SQL_PORT + "/"
					+ SQL_DATA;
			con = DriverManager.getConnection(conn, SQL_USER, SQL_PASS);
		} catch (ClassNotFoundException ex) {
			log.warning("No MySQL driver found!");
		} catch (SQLException ex) {
			log.warning("Error while fetching MySQL connection!");
			log.warning(ex.getMessage());
		} catch (Exception ex) {
			log.warning("Unknown error while fetchting MySQL connection.");
		}
	}

    @Getter
	public static Connection SQLConnection = con;

	public static void SQLquery(String sql) {
		BGQuery bq = new BGQuery(sql, log, con, instance);
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(bq);
		executor.shutdown();
	}

	public static void SQLdisconnect() {
		BGEndDB end = new BGEndDB(instance, log, con);
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.execute(end);
		executor.shutdown();
	}
	
	public static int getCoins(UUID player) {
		try {
			Statement stmt = con.createStatement();
			ResultSet r = stmt
					.executeQuery("SELECT `COINS`, `REF_PLAYER` FROM `REWARD` WHERE `REF_PLAYER` = "
							+ player + " ;");
			r.last();
			if (r.getRow() == 0) {
				stmt.close();
				r.close();
				return 0;
			}
			Integer PL_ID = r.getInt("COINS");
			stmt.close();
			r.close();
			return PL_ID;
		} catch (SQLException ex) {
			log.warning("Error with following query: "
					+ "SELECT `COINS`, `REF_PLAYER` FROM `REWARD` WHERE `REF_PLAYER` = "
					+ player + " ;");
			System.err.println("MySQL-Error: " + ex.getMessage());
			return 0;
		} catch (NullPointerException ex) {
			log.warning("Error while performing a query. (NullPointerException)");
			return 0;
		}
	}
	
	public static void addGameMaker(Player p) {
		spectators.remove(p);
		gamemakers.add(p);
		
		p.setGameMode(GameMode.CREATIVE);
		BGVanish.makeVanished(p);
		BGChat.printPlayerChat(p, ChatColor.YELLOW + Translation.NOW_GAMEMAKER.t());
	}
	
	public static void remGameMaker(Player p) {
		gamemakers.remove(p);
		p.setGameMode(GameMode.SURVIVAL);
		BGVanish.makeVisible(p);
	}
	
	public static boolean isGameMaker(Player p) {
		return gamemakers.contains(p);
	}
	
	public static boolean isSpectator(Player p) {
		if(isGameMaker(p))
			return false;
		
		return spectators.contains(p);
	}
	
	public static void addSpectator(Player p) {
		if(isGameMaker(p))
			return;
			
		spectators.add(p);
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(true);
		p.setFlying(true);
		BGVanish.makeVanished(p);
		for(int i=0;i<=8;i++) {
			p.getInventory().setItem(i, new ItemStack(Material.CROPS, 1));
		}
		BGChat.printPlayerChat(p, ChatColor.YELLOW + Translation.NOW_SPECTATOR.t());
	}
	
	public static void remSpectator(Player p) {
		spectators.remove(p);
		p.setGameMode(GameMode.SURVIVAL);
		p.getInventory().clear();
		BGVanish.makeVisible(p);
	}
	
	public static ArrayList<Player> getOnlineOps() {
		ArrayList<Player> ops = new ArrayList<>();
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.isOp())
				ops.add(p);
		}
				
		return ops;
	}
}
