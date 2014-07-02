package threads;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;


import main.BGMain;

public class BGQuery extends Thread{
	
	private String sql;
	private Logger log;
	private Connection con;
	
	public BGQuery(String sql, Logger log, Connection con, Plugin plugin) {
		
		setDaemon(false);
		
		this.sql = sql;
		this.log = log;
		this.con = con;
	}
	
	@Override
	public void run() {
		BGMain.lock.lock();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException ex) {
			log.warning("Error with following query: "
					+ sql);
			log.warning(ex.getMessage());
			BGMain.SQLdisconnect();
		} catch (NullPointerException ex) {
			log.warning("Error while performing a query. (NullPointerException)");
		}
		BGMain.lock.unlock();
	}
}
