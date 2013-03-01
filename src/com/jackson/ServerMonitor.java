package com.jackson;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

public class ServerMonitor {
	private static final Logger logger = Logger.getLogger(ServerMonitor.class);
	private final SQLResponseTimeWriter sqlResponseTimeWriter;
	private static final int MILLISECONDS_IN_SECOND = 1000;
	private static final int SOCKET_TIMEOUT = 1000;
	
	public ServerMonitor(SQLConnection sqlConnection){
		this.sqlResponseTimeWriter = new SQLResponseTimeWriter(sqlConnection);	
	}	
	
	public void checkEndpoint(final Endpoint endpoint){
		Socket socket = null;
		final long timeStart;
		final long timeStop;
		try{						
			long epoch;
			/* creates a socket with each endpoint, starts a timer, checks the connection, stops the timer, gets the current time, 
			 * and then calls the method to insert the results into the response_times table. */
			socket = new Socket();				
			socket.connect(new InetSocketAddress(endpoint.getHost(), endpoint.getPort()), SOCKET_TIMEOUT);
			timeStart = System.nanoTime();
			if (socket.isConnected() == true){
				timeStop = System.nanoTime();
				epoch = System.currentTimeMillis()/MILLISECONDS_IN_SECOND;
				sqlResponseTimeWriter.insertResponseTime(endpoint.getHost(), endpoint.getPort(), epoch, (float)((timeStop - timeStart)/1000000.0));
				socket.close();
			}
		}
		catch(UnknownHostException e) {
			logger.debug(endpoint.getHost() + ":" + endpoint.getPort() + ",UnknownHostException");
			sqlResponseTimeWriter.recordTimeout(endpoint);
		}
		catch(IOException e){
			logger.debug(endpoint.getHost() + ":" + endpoint.getPort() + ",IOException");
			sqlResponseTimeWriter.recordTimeout(endpoint);
		}
	} // end of checkEndpoints()	
} // end of ServerMonitor