package com.jackson;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerMonitor {	
	
	//new
	private final ResponseTimeManager responseTimeManager;
	//end new
	public ServerMonitor(SQLConnector sqlConnector){
		this.responseTimeManager = new ResponseTimeManager(sqlConnector);
		//endpointManager.loadEndpoints(fileName);		
	}

	public void checkEndpoint(final Endpoint endpoint) throws UnknownHostException, IOException {
		Socket socket = null;			
		long epoch;
		/* creates a socket with each endpoint, starts a timer, checks the connection, stops the timer, gets the current time, 
		 * and then calls the method to insert the results into the response_times table. */
		socket = new Socket();				
		socket.connect(new InetSocketAddress(endpoint.getHost(), endpoint.getPort()), 1000);
		Timer timer = new Timer();
		timer.start();
		if (socket.isConnected() == true){
			timer.stop();
			epoch = System.currentTimeMillis()/1000;
			System.out.println(endpoint.getHost() + ":" + endpoint.getPort() + " responded in " + timer.duration() + " ms at " + epoch);
			//new, see timer.java for float cast of timer.duration()
			responseTimeManager.insertResponseTime(endpoint.getHost(), endpoint.getPort(), epoch, (float) timer.duration());
			//end new
			timer.reset();
		}
		else {
			System.err.println("Could not connect to " + endpoint.getHost() + ":" + endpoint.getPort());
		}
		socket.close();
	} // end of checkEndpoints()	
} // end of ServerMonitor