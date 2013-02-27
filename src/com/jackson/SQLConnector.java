/* SQLConnector sets up the connection to the DBMS and leaves it open.  It also
 * creates the database and both tables if they don't already exist. */

package com.jackson;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnector {
	
	private Connection connection;
	
	public SQLConnector(String host, String user, String pass){		
		Statement statement;
		try {
			connection = DriverManager.getConnection(host, user, pass);
			statement = connection.createStatement();
			statement.executeUpdate("CREATE DATABASE IF NOT EXISTS server_monitor_db;");
			statement.execute("USE server_monitor_db");
			statement.executeUpdate( "CREATE TABLE IF NOT EXISTS endpoints ("+
									"eid SMALLINT UNSIGNED AUTO_INCREMENT NOT NULL, " + 
									"ip VARCHAR(15) NOT NULL, " +
									"port SMALLINT UNSIGNED NOT NULL, " +
									"PRIMARY KEY ( eid ), " +
									"UNIQUE(ip, port));");
			statement.executeUpdate( "CREATE TABLE IF NOT EXISTS response_times ( " +
									"eid SMALLINT UNSIGNED NOT NULL, " +
									"timestamp BIGINT UNSIGNED NOT NULL, " +
									"latency FLOAT NOT NULL, " +
									"INDEX(eid), " +
									"FOREIGN KEY(eid) REFERENCES endpoints(eid) ON " +
									"DELETE CASCADE ON UPDATE CASCADE);");
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS response_timeouts (" +
									"eid SMALLINT UNSIGNED NOT NULL," +
									"timestamp BIGINT UNSIGNED NOT NULL," +
									"index(eid)," +
									"FOREIGN KEY(eid) REFERENCES endpoints(eid) ON DELETE CASCADE ON UPDATE CASCADE);");
			statement.close();			
		} 
		catch (SQLException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} // end try
	} // end constructor SQLConnector()	
	
	public void closeConnection() {
		try {		
			if(connection != null) connection.close();			
		} 
		catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		finally {
			connection = null;
		}
	}
	
	public Connection getConnection(){
		return connection;
	}
}
