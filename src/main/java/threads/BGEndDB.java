package threads;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;


import main.BGMain;

public class BGEndDB extends Thread{

	private Logger log;
	private Connection con;
	
	public BGEndDB(BGMain plugin, Logger log, Connection con) {
		setDaemon(false);
		this.log = log;
		this.con = con;
	}
	
	@Override
	public void run() {
		BGMain.lock.lock();
		try {
			log.warning("Disconnecting from MySQL database...");
			con.close();
		} catch (SQLException ex) {
			log.warning("Error while closing the connection...");
			log.warning(ex.getMessage());
		} catch (NullPointerException ex) {
			log.warning("Error while closing the connection...");
		}

		if(!BGMain.SQL_DSC){
			log.info("Reconnecting with MySQL database...");
			BGMain.SQLconnect();
		}
		
		BGMain.lock.unlock();
	}
}
