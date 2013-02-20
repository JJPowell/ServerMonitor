/* Endpoint is an object for holding and then retrieving
 * an IP and port combination. */

package com.jackson;

public class Endpoint {		
	
	private String host;
	private int port;
	
	public Endpoint(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public String getHost(){
		return host;
	}
	
	public int getPort(){
		return port;
	}
} // end of Endpoint
