package com.jackson;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.log4j.Logger;

public class ServerMonitor {
	private static final Logger logger = Logger.getLogger(ServerMonitor.class);
	private final ResponseTimeManager responseTimeManager;
	private static final int MILLISECONDS_IN_SECOND = 1000;
	private static final int SOCKET_TIMEOUT = 1000;
	
	public ServerMonitor(SQLConnector sqlConnector){
		this.responseTimeManager = new ResponseTimeManager(sqlConnector);	
	}	
	
	public void checkEndpoint(final Endpoint endpoint){
		try{
			Socket socket = null;			
			long epoch;
			/* creates a socket with each endpoint, starts a timer, checks the connection, stops the timer, gets the current time, 
			 * and then calls the method to insert the results into the response_times table. */
			socket = new Socket();				
			socket.connect(new InetSocketAddress(endpoint.getHost(), endpoint.getPort()), SOCKET_TIMEOUT);
			Timer timer = new Timer();
			timer.start();
			if (socket.isConnected() == true){
				timer.stop();
				epoch = System.currentTimeMillis()/MILLISECONDS_IN_SECOND;
				responseTimeManager.insertResponseTime(endpoint.getHost(), endpoint.getPort(), epoch, (float) timer.duration());
				timer.reset();
				socket.close();
			}
		}
		catch(UnknownHostException e) {
			logger.debug(endpoint.getHost() + ":" + endpoint.getPort() + ",UnknownHostException");
		}
		catch(IOException e){
			logger.debug(endpoint.getHost() + ":" + endpoint.getPort() + ",IOException");
		}
	} // end of checkEndpoints()	
} // end of ServerMonitor