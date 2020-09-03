package pkg;

import java.util.*;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
public class LoginDB 
{  
	public AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	public DynamoDB dynamoDB = new DynamoDB(client);
	public String tableName = "loginTable";
	
	/*Table loginTable created wtih username as primary key. */
	public boolean createLoginTable()
	{  
		try 
		{
			List<AttributeDefinition> attDef = new ArrayList<AttributeDefinition>();
			attDef.add(new AttributeDefinition().withAttributeName("username").withAttributeType("S"));
			
			List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
			keySchema.add(new KeySchemaElement().withAttributeName("username").withKeyType(KeyType.HASH));
			
			CreateTableRequest request = new CreateTableRequest()
					.withTableName("loginTable")
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
	/*validate that user exists in database by validating the username and password entered match the 
	 * username and password in the database.*/
	public boolean validateLogin(String username, String password)
	{
		try {
			Table table = dynamoDB.getTable(tableName);
			QuerySpec spec = new QuerySpec()
					.withKeyConditionExpression("username = :v_username")
					.withFilterExpression("password = :v_password")
					.withValueMap(new ValueMap()
							.withString(":v_username", username)
							.withString(":v_password", password));
			ItemCollection<QueryOutcome> items = table.query(spec);
	
			Iterator<Item> iterator = items.iterator();
			int size = 0;
			while (iterator.hasNext()) 
			{ 
				iterator.next();
			    size += 1;	
			}
			
			if (size == 1)	//if found username and password return true
				return true;
		} catch (Exception ex) {
		return false; }
		return false;
	}
	/*Add user to login table to contain username(hash), password, firstName, lastName, and movie_ids(ids of movies saved).*/
	public boolean addUser(String username, String password, String firstName, String lastName)
	{
		try {
		List<String> movie_ids = new ArrayList<String>();
		Table table = dynamoDB.getTable(tableName);
			Item item = new Item()
					.withPrimaryKey("username", username)
					.withString("password",password)
					.withString("firstName", firstName)
					.withString("lastName", lastName)
					.withList("movie_ids", movie_ids);
			
			table.putItem(item);
		}
		catch (Exception ex) {return false;}
		return true;
	}
	/*Check if the table exists in AWS's account with entered tableName*/
	public boolean tableExist(String tableName)
	{
		try {
		ListTablesRequest request = new ListTablesRequest();
		ListTablesResult response = client.listTables(request);
		List<String> list = response.getTableNames();
		return list.contains(tableName);
		}
		catch (Exception ex) {return false;}
	}
	/*Add a movie id to the list of movie ids for a particular user, validates to only
	 * enter movie ids that are not already in the list. */
	public boolean addMovie( String username, String movie_id)
	{
		try {
		Table table = dynamoDB.getTable(tableName);

		List<String> temp2 = getMoviesforUser(username);
		
		if (!temp2.contains(movie_id))
		{
			List<String> temp = new ArrayList<String>();
			temp.add(movie_id);
			Map<String, String> expressionAttributeNames = new HashMap<String, String>();
			expressionAttributeNames.put("#messages", "movie_ids");

			Map<String, Object> expressionAttributeValues = new HashMap<String, Object>();
			expressionAttributeValues.put(":message", temp);
			UpdateItemOutcome outcome =  table.updateItem(
			    "username",          // key attribute name
			    username,           // key attribute value
			    "set #messages = list_append(#messages, :message)", // UpdateExpression
			    expressionAttributeNames,
			    expressionAttributeValues);
		}
		return true;
		} catch (Exception ex) {return false;}
	}
	/*Return list of movie ids for user.*/
	public List<String> getMoviesforUser(String username)
	{
		try {
		Table table = dynamoDB.getTable(tableName);

		GetItemSpec spec = new GetItemSpec()
			    .withPrimaryKey("username", username)
			    .withProjectionExpression("movie_ids")
			    .withConsistentRead(true);
		Item item = table.getItem(spec);
		List<String> temp2 = item.getList("movie_ids");
		return temp2;
		}
		catch (Exception ex) {return null;}
	}
	/*Remove movie id from user's list.*/
	public boolean removeMovie(String username, String movieId)
	{
		try {
		List<String> userMovies = getMoviesforUser(username);
		int movieIndex = -1;
		if (userMovies.contains(movieId))
			movieIndex = userMovies.indexOf(movieId);
		System.out.println(movieIndex);
		Table table = dynamoDB.getTable(tableName);
		String update = "Remove movie_ids["+movieIndex+"]";

		 UpdateItemSpec updateItemSpec = new UpdateItemSpec()
				 .withPrimaryKey("username", username)
				 .withUpdateExpression(update)
				 .withReturnValues(ReturnValue.ALL_NEW);

	    UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
		return true;
		} catch (Exception ex) {return false;}
	}
}
