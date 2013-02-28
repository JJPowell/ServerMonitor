/* ResponseTimeManager uses an IP and port to find the correct eid from the endpoints
 * table and then inserts the eid, timestamp, and latency into the response_times table. */

package com.jackson;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class ResponseTimeManager {
	private static final Logger logger = Logger.getLogger(ResponseTimeManager.class);
	private SQLConnector sqlConnector;	
	
	public ResponseTimeManager(SQLConnector sqlConnector){
		this.sqlConnector = sqlConnector;
	}
	
	public void insertResponseTime(String ip, int port, long timeStamp, float latency){
		try{			
			PreparedStatement ps = sqlConnector.getConnection().prepareStatement(
					"INSERT INTO response_times (eid, timestamp, latency) " + 
					"VALUES ((SELECT eid FROM endpoints WHERE ip=? AND port=?), " +
					"UNIX_TIMESTAMP(NOW()), ?);");			
			ps.setString(1, ip);
			ps.setInt(2, port);
			ps.setFloat(3, latency);
			ps.executeUpdate();
			ps.close();
		} // end try
		catch(SQLException e){
			logger.debug(",SQLException");
		} 
	} // end insertResponseTime()
	
	public void recordTimeout(final Endpoint endpoint){
		try{
			PreparedStatement ps = sqlConnector.getConnection().prepareStatement(
					"INSERT INTO response_timeouts (eid, timestamp) " + 
					"VALUES ((SELECT eid FROM endpoints WHERE ip= ? AND port= ?), " +
					"UNIX_TIMESTAMP(NOW()));");
			ps.setString(1, endpoint.getHost());
			ps.setInt(2, endpoint.getPort());
			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e){
			logger.debug(",SQLException");
		}
	}
} // end class ResponseTimeManager
