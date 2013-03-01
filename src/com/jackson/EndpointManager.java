package com.jackson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;

public class EndpointManager {
	private final SQLConnection sqlConnection;
	private final ArrayList<Endpoint> endpoints;
	private static final Logger logger = Logger.getLogger(EndpointManager.class);
	
	public EndpointManager(final String fileName, final SQLConnection sqlConnection){
		this.sqlConnection = sqlConnection;
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
			while((line = input.readLine()) != null){
				line = line.trim();
				if(line.isEmpty()){
					continue;
				}
				array = line.split(":");
				endpoint = new Endpoint(array[0], Integer.parseInt(array[1]));			
				insertEndpoint(endpoint);
			}
			input.close();
		}
		catch (FileNotFoundException e){
			logger.debug(",FileNotFoundException");
		}
		catch (IOException e){ 
			logger.debug(",IOException");
		}
	} // end of loadEndpoints()
	
	private void insertEndpoint(final Endpoint endpoint){
		endpoints.add(endpoint);//adds the endpoint into the endpoints ArrayList.
		try{
			//inserts the endpoint into the endpoints table only if it doesn't already exist.
			PreparedStatement ps = sqlConnection.getConnection().prepareStatement("INSERT INTO endpoints (ip, port) VALUES (?, ?)" + 
																				 "ON DUPLICATE KEY UPDATE ip = ?, port = ?");
			ps.setString(1, endpoint.getHost());
			ps.setInt(2, endpoint.getPort());
			ps.setString(3, endpoint.getHost());
			ps.setInt(4, endpoint.getPort());
			ps.executeUpdate();
			ps.close();
		}
		catch(SQLException e){
			logger.debug(",SQLException");
		}
	} // end of insertEndpoint()
	
	public ArrayList<Endpoint> getEndpoints(){		
		return endpoints;		
	}
	
} // end of class EndpointManager
