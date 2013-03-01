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
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Main {
	private static final Logger logger = Logger.getLogger(Main.class);
	private static final int SLEEP_IN_MILLISECONDS = 5000;
	private static final String PROPERTY_CONFIGURATION_FILE = "/home/jackson/projects/ServerMonitor/log4j.properties";
	
	public static void main(String[] args) {		
		final SQLConnection sqlConnection = new SQLConnection("jdbc:mysql://localhost:3306/", "jackson", "jpassword");
		final EndpointManager endpointManager = new EndpointManager("/home/jackson/servers.txt", sqlConnection);
		final ServerMonitor serverMonitor = new ServerMonitor(sqlConnection);
		
		PropertyConfigurator.configure(PROPERTY_CONFIGURATION_FILE);
		ArrayList<Endpoint> endpoints = endpointManager.getEndpoints();
		System.out.println("Currently monitoring " + endpoints.size() + " ports.");
		while(true){
			for(Endpoint endpoint : endpoints){
				serverMonitor.checkEndpoint(endpoint);
			} // end for
			try {
				Thread.sleep(SLEEP_IN_MILLISECONDS);
			} 
			catch (InterruptedException e) {
				logger.debug(",InterruptedException");
			}
		} // end of while(true)
	} // end of main()
} // end of class Main
