package pkg;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;



public class MovieDB 
{
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	DynamoDB dynamoDB = new DynamoDB(client);
	String movieDBname = "movie_list";
	
	/*
	 * Creates movie table with movie_id as the hash and title as the range. 
	 * */
	public boolean createMovieTable()
	{
		try 
		{
			List<AttributeDefinition> attDef = new ArrayList<AttributeDefinition>();
			attDef.add(new AttributeDefinition().withAttributeName("movie_id").withAttributeType("N"));
			attDef.add(new AttributeDefinition().withAttributeName("title").withAttributeType("S"));
			
			List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
			keySchema.add(new KeySchemaElement().withAttributeName("movie_id").withKeyType(KeyType.HASH));
			keySchema.add(new KeySchemaElement().withAttributeName("title").withKeyType(KeyType.RANGE));
			
			CreateTableRequest request = new CreateTableRequest()
					.withTableName(movieDBname)
					.withKeySchema(keySchema)
					.withAttributeDefinitions(attDef)
					.withProvisionedThroughput(new ProvisionedThroughput()
							.withReadCapacityUnits(5L)
							.withWriteCapacityUnits(6L));
			
			Table table = dynamoDB.createTable(request);
			table.waitForActive();
		
		}
		catch (InterruptedException e) 
		{
			return false;
		}
	
	return true; 
	}
	/*
	 * Adds movies from the movieList to the movie table 
	 * */
	public boolean addMovies(String tableName, ArrayList<Movie> movieList)
	{
		try
		{ 
			Table table = dynamoDB.getTable(movieDBname);
			for (Movie movie: movieList)
			{
				Item item = new Item()
						.withPrimaryKey("movie_id", movie.movie_id, "title", movie.title)
						.withString("release_date", movie.release_date)
						.withList("genres", movie.genres)
						.withList("genres_id", movie.genre_ids)
						.withString("movie_poster", movie.posterpath)
						.withString("overview", movie.overview);
				table.putItem(item);
			}
		} 
		catch(Exception ex ) {}
		return true;		
	}
	//Returns list of movies if one of the genres is the specified genre. 
	public ArrayList<Movie> queryByGenre(String genre)
	{
		try {
		Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
	    expressionAttributeValues.put(":gen", new AttributeValue().withS(genre));	    
	   
	    ScanRequest scanRequest = new ScanRequest()
	    		.withTableName(movieDBname)
	    		.withFilterExpression("contains(genres,:gen)")
	    		.withExpressionAttributeValues(expressionAttributeValues);
	    
	    
	    ScanResult result = client.scan(scanRequest);
	    ArrayList<Movie> tempList = new ArrayList<Movie>();
	    for (Map<String, AttributeValue> item: result.getItems())
	    {
	    	Movie temp = new Movie();
	    	List<AttributeValue> list = new ArrayList<AttributeValue>(item.values());
	    	temp.overview = list.get(1).getS();
	    	temp.release_date = list.get(2).getS();
	    	List<AttributeValue> tempGen = list.get(3).getL();
	    	ArrayList<String> an = new ArrayList<String>();
	    	for (AttributeValue tempA: tempGen)
	    	{
	    		an.add(tempA.getS());
	    	}
	    	temp.genres = an;
	    	temp.movie_id = Double.parseDouble(list.get(4).getN());
	    	temp.posterpath = list.get(5).getS();
	    	temp.title = list.get(6).getS();
	    	tempList.add(temp);    	
	    }	   
	    return tempList;	
		} catch (Exception ex) {return null;}
	}
	//Returns list of all movies in the movie table 
	public ArrayList<Movie> queryAllGenres()
	{
		try {
	    ArrayList<Movie> tempList = new ArrayList<Movie>();		 
	    ScanResult result = null;
	    do
	    {
	        ScanRequest req = new ScanRequest();
	        req.setTableName(movieDBname);
	 
	        if(result != null)
	        {
	            req.setExclusiveStartKey(result.getLastEvaluatedKey());
	        }         
	        result = client.scan(req);
	 

	        for(Map<String, AttributeValue> item : result.getItems())
	        {
		    	Movie temp = new Movie();
		    	List<AttributeValue> list = new ArrayList<AttributeValue>(item.values());
		    	temp.overview = list.get(1).getS();
		    	temp.release_date = list.get(2).getS();
		    	List<AttributeValue> tempGen = list.get(3).getL();
		    	ArrayList<String> an = new ArrayList<String>();
		    	for (AttributeValue tempA: tempGen)
		    	{
		    		an.add(tempA.getS());
		    	}
		    	temp.genres = an;
		    	temp.movie_id = Double.parseDouble(list.get(4).getN());
		    	temp.posterpath = list.get(5).getS();
		    	temp.title = list.get(6).getS();
		    	tempList.add(temp);   
	        }
	    } 
	    while(result.getLastEvaluatedKey() != null);	    
	    return tempList;
		}
	    catch (Exception ex) {return null;}
	}
	//Query specific movie by id (hash)
	public Movie queryById(String id)
	{
		try {
		Map<String, AttributeValue> expressionAttributeValues = 
			    new HashMap<String, AttributeValue>();
			expressionAttributeValues.put(":val", new AttributeValue().withN(id)); 
			        
			ScanRequest scanRequest = new ScanRequest()
			    .withTableName(movieDBname)
			    .withFilterExpression("movie_id = :val")
			    .withExpressionAttributeValues(expressionAttributeValues);


			ScanResult result = client.scan(scanRequest);
			Movie temp = new Movie();
			for (Map<String, AttributeValue> item : result.getItems()) 
			{		    	
				List<AttributeValue> list = new ArrayList<AttributeValue>(item.values());
		    	temp.overview = list.get(1).getS();
				temp.release_date = list.get(2).getS();
				List<AttributeValue> tempGen = list.get(3).getL();
				ArrayList<String> an = new ArrayList<String>();
				for (AttributeValue tempA: tempGen)
				{
					an.add(tempA.getS());
				}
				temp.genres = an;
		    	temp.movie_id = Double.parseDouble(list.get(4).getN());
		    	temp.posterpath = list.get(5).getS();
		    	temp.title = list.get(6).getS();
			}
			return temp;
		}catch (Exception ex) { return null;}
	}
	/*Return Movies associated with user's movie_ids.*/
	public ArrayList<Movie> userMovies(String username)
	{
		ArrayList<Movie> movies = new ArrayList<Movie>();
		List<String> user_ids = new LoginDB().getMoviesforUser(username);
		for (String id: user_ids)
		{
			movies.add(queryById(id));
		}
		
		return movies;
	}
	//sort movies by title or by release date
	public ArrayList<Movie> sortby(ArrayList<Movie> movies, String sort)
	{
		List<Movie> tempMovies = movies;
		if (sort.equals("title"))
			Collections.sort(tempMovies, new Sortbytitle());
		else

			Collections.sort(tempMovies, new Sortbyreleasedate());
		return (ArrayList<Movie>) tempMovies;		
	}
}
