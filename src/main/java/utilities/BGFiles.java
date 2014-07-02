package utilities;

import java.io.File;
import java.util.logging.Logger;

import main.BGMain;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class BGFiles {	
	Logger log = BGMain.getPluginLogger();
	
	public static FileConfiguration abconf;
	public static FileConfiguration bookconf;
	public static FileConfiguration config;
	public static FileConfiguration dsign;
	public static FileConfiguration kitconf;
	public static FileConfiguration cornconf;
	public static FileConfiguration feastconf;
	public static FileConfiguration worldconf;

	public BGFiles() {		
		try{
			loadFiles();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadFiles() throws Exception {
		File configFile = new File(BGMain.instance.getDataFolder(), "config.yml");
		File kitFile = new File(BGMain.instance.getDataFolder(), "kit.yml");
		File leaderboardFile = new File(BGMain.instance.getDataFolder(), "leaderboard.yml");
		File deathSignFile = new File(BGMain.instance.getDataFolder(), "deathsign.yml");
		File abilitiesFile = new File(BGMain.instance.getDataFolder(), "abilities.yml");
		File bookFile = new File(BGMain.instance.getDataFolder(), "book.yml");
		File cornFile = new File(BGMain.instance.getDataFolder(), "cornucopia.yml");
		File feastFile = new File(BGMain.instance.getDataFolder(), "feast.yml");
		File worldFile = new File(BGMain.instance.getDataFolder(), "world.yml");

		Integer creation = 0;
		
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			BGMain.copy(BGMain.instance.getResource("config.yml"), configFile);
			creation++;
		}
		if (!kitFile.exists()) {
			kitFile.getParentFile().mkdirs();
			BGMain.copy(BGMain.instance.getResource("kit.yml"), kitFile);
			creation++;
		}
		if (!leaderboardFile.exists()) {
			leaderboardFile.getParentFile().mkdirs();
			BGMain.copy(BGMain.instance.getResource("leaderboard.yml"), leaderboardFile);
			creation++;
		}
		if (!deathSignFile.exists()) {
			deathSignFile.getParentFile().mkdirs();
			BGMain.copy(BGMain.instance.getResource("deathsign.yml"), deathSignFile);
			creation++;
		}
		if (!abilitiesFile.exists()) {
			abilitiesFile.getParentFile().mkdirs();
			BGMain.copy(BGMain.instance.getResource("abilities.yml"), abilitiesFile);
			creation++;
		}
		if (!bookFile.exists()) {
			bookFile.getParentFile().mkdirs();
			BGMain.copy(BGMain.instance.getResource("book.yml"), bookFile);
			creation++;
		}
		if(!cornFile.exists()) {
			cornFile.getParentFile().mkdirs();
			BGMain.copy(BGMain.instance.getResource("cornucopia.yml"), cornFile);
			creation++;
		}
		if(!feastFile.exists()) {
			feastFile.getParentFile().mkdirs();
			BGMain.copy(BGMain.instance.getResource("feast.yml"), feastFile);
			creation++;
		}
		if(!worldFile.exists()) {
			worldFile.getParentFile().mkdirs();
			BGMain.copy(BGMain.instance.getResource("world.yml"), worldFile);
			creation++;
		}
		
		if(creation > 0)
			log.info("Created " + creation + " files.");
		
						
		abconf = YamlConfiguration.loadConfiguration(
				new File(BGMain.instance.getDataFolder(), "abilities.yml"));
		bookconf = YamlConfiguration.loadConfiguration(
				new File(BGMain.instance.getDataFolder(), "book.yml"));
		config = YamlConfiguration.loadConfiguration(
				new File(BGMain.instance.getDataFolder(), "config.yml"));
		dsign = YamlConfiguration.loadConfiguration(
				new File(BGMain.instance.getDataFolder(), "deathsign.yml"));
		kitconf = YamlConfiguration.loadConfiguration(
				new File(BGMain.instance.getDataFolder(), "kit.yml"));
		cornconf = YamlConfiguration.loadConfiguration(
				new File(BGMain.instance.getDataFolder(), "cornucopia.yml"));
		feastconf = YamlConfiguration.loadConfiguration(
				new File(BGMain.instance.getDataFolder(), "feast.yml"));
		worldconf = YamlConfiguration.loadConfiguration(
				new File(BGMain.instance.getDataFolder(), "world.yml"));
	}
}
