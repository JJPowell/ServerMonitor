/* This was written as a utility to check if certain IPs are open and accepting connections on
 * certain ports.  Whatever servers you have listed in servers.txt will be checked every 5 
 * seconds until the program is closed.  Whatever is in servers.txt is read into an
 * ArrayList and cycled through each time the serverCheck.checkEndpoints() method is called.
 * 
 * Instructions:
 * 1. MySQL needs to be set up and already running.
 * 2. You need a user with create, read, and write for whatever your database is named
 * 3. Every line of servers.txt needs to be in the format "ip:port" e.g. 192.168.1.1:80 */

package com.jackson;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Main {
	
	public static void main(String[] args) {		
		final SQLConnector sqlConnector = new SQLConnector("jdbc:mysql://localhost:3306/", "jackson", "jpassword");
		final EndpointManager endpointManager = new EndpointManager("/home/jackson/servers.txt", sqlConnector);
		final ServerMonitor serverMonitor = new ServerMonitor(sqlConnector);
		final ResponseTimeManager responseTimeManager = new ResponseTimeManager(sqlConnector);
		
		ArrayList<Endpoint> endpoints = endpointManager.getEndpoints();
		while(true){
			for(Endpoint endpoint : endpoints){
				try{
					serverMonitor.checkEndpoint(endpoint);
				}
				catch(UnknownHostException e){
					System.err.println(endpoint.getHost() + ":" + String.valueOf(endpoint.getPort()) + " " + e.getMessage());
					responseTimeManager.recordTimeout(endpoint);
				}
				catch(IOException e){
					System.err.println(endpoint.getHost() + ":" + String.valueOf(endpoint.getPort()) + " " + e.getMessage());
					responseTimeManager.recordTimeout(endpoint);
				}
			}
			try {
				Thread.sleep(5000);
			} 
			catch (InterruptedException e) {
				// intentionally ignoring exception
			}
		} // end of while(true)
	} // end of main()
} // end of class Main
