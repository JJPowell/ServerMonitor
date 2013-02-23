package com.jackson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class EndpointManager {
	private final SQLConnector sqlConnector;
	private final ArrayList<Endpoint> endpoints;
	
	public EndpointManager(final String fileName, final SQLConnector sqlConnector){
		this.sqlConnector = sqlConnector;
		this.endpoints =  new ArrayList<Endpoint>();
		loadEndpoints(fileName);
	}

	private void loadEndpoints(final String fileName){
		try{
			BufferedReader input = new BufferedReader(new FileReader(fileName));			
			String line;
			String[] array;			
			Endpoint endpoint;
			/* reads each line of servers.txt and then converts them into Endpoint
			 * objects to be inserted into the endpointManager. */
			System.out.println("Endpoints:");//remove this if removing next println()
			while((line = input.readLine()) != null){
				line = line.trim();
				if(line.isEmpty())
					continue;
				array = line.split(":");
				endpoint = new Endpoint(array[0], Integer.parseInt(array[1]));
				System.out.println(array[0] + " " + Integer.parseInt(array[1]));//remove this if removing previous println()				
				insertEndpoint(endpoint);
			}
			input.close();
		}
		catch (FileNotFoundException f){
			System.err.println(f);
		}
		catch (IOException e){ 
			System.err.println(e);
		}
	} // end of loadEndpoints()
	
	private void insertEndpoint(final Endpoint endpoint){
		endpoints.add(endpoint);//adds the endpoint into the endpoints ArrayList.
		try{
			//inserts the endpoint into the endpoints table only if it doesn't already exist.
			PreparedStatement ps = sqlConnector.getConnection().prepareStatement("INSERT INTO endpoints (ip, port) VALUES (?, ?)" + 
																				 "ON DUPLICATE KEY UPDATE ip = ?, port = ?");
			ps.setString(1, endpoint.getHost());
			ps.setInt(2, endpoint.getPort());
			ps.setString(3, endpoint.getHost());
			ps.setInt(4, endpoint.getPort());
			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e){
			System.err.println(e.getMessage());
		}
	} // end of insertEndpoint()
	
	void recordTimeout(final Endpoint endpoint){
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
			System.err.println(e.getMessage());
		}
	}
	
	public ArrayList<Endpoint> getEndpoints(){		
		return endpoints;		
	}
	
} // end of class EndpointManager
