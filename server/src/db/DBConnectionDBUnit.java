package db;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dbunit.DatabaseTestCase;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlConnection;
import org.dbunit.operation.DatabaseOperation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class DBConnectionDBUnit extends DatabaseTestCase{
	protected IDatabaseConnection getConnection() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/unittest?", "root", "root"
				);
		return new MySqlConnection(conn, "unittest");
	}

	@Override
    protected IDataSet getDataSet() throws Exception
    {
        return new FlatXmlDataSetBuilder().build(
        		new FileInputStream("src/db/DBUnitTest.xml")
        		);
	}

	@Override
	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT; // by default (will do DELETE_ALL + INSERT)
	}

	@Override
	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.NONE;
	}

	@Test
	public void testGetVisitedRestaurants() throws Exception{
		DBConnection dbconn = new DBConnection(
				"jdbc:mysql://localhost:3306/unittest?user=root&password=root");
		String userId = "1111";
		Set<String> visited = dbconn.getVisitedRestaurants(userId);
		assertEquals( new HashSet<String>(
				Arrays.asList("--qeSYxyn62mMjWvznNTdg", "--zgHBiQpr8H2ZqSdGmguQ",
				"-1B-DEGkLE1kDj5ENAF2NQ")), visited);
		dbconn.close();
	}
	
	@Test
	public void testSetVisitedRestaurants() throws Exception {
		DBConnection dbconn = new DBConnection(
				"jdbc:mysql://localhost:3306/unittest?user=root&password=root");
		String userId = "1111";
		List<String> businessIds = Arrays.asList("-0HGqwlfw3I8nkJyMHxAsQ");
		dbconn.SetVisitedRestaurants(userId, businessIds);
		Set<String> visited = dbconn.getVisitedRestaurants(userId);
		assertEquals( new HashSet<String>(Arrays.asList(
				"--qeSYxyn62mMjWvznNTdg", "--zgHBiQpr8H2ZqSdGmguQ",
				"-1B-DEGkLE1kDj5ENAF2NQ","-0HGqwlfw3I8nkJyMHxAsQ")), visited);
		dbconn.close();
	}

	@Test
	public void testRecommendRestaurants() throws Exception {
		DBConnection dbconn = new DBConnection(
				"jdbc:mysql://localhost:3306/unittest?user=root&password=root");
		String userId = "1111";
		JSONArray restaurants = dbconn.RecommendRestaurants(userId);
		Set<String> recommend = new HashSet<>();
        int length = restaurants.length();
        for(int i = 0; i < length; i++){
            JSONObject restaurant = restaurants.getJSONObject(i);
            recommend.add(restaurant.getString("business_id"));  
        }  
        Set<String> expected = new HashSet<String>(Arrays.asList(
        		"--jFTZmywe7StuZ2hEjxyA", "-1BzcQK-HDA6LVOThHMpsw", 
        		"--UE_y6auTgq3FXlvUMkbw", "--Y_2lDOtVDioX5bwF6GIw", 
        		"-1ERbsOf9XOC9wGbZYFr7g", "-024YEtnIsPQCrMSHCKLQw", 
        		"--BlvDO_RG2yElKu9XA1_g", "-0tJ4FwcNtJjil-ZUTy30Q", 
        		"-0lOuL7RkZQnjAl96dXTvA", "--pOlFxITWnhzc7SHSIP0A"));
        assertEquals(expected, recommend);
		dbconn.close();
	}

	@Test
	public void testGetRestaurantsNearLoation() throws Exception {
		DBConnection dbconn = new DBConnection(
				"jdbc:mysql://localhost:3306/unittest?user=root&password=root");
		JSONArray restaurants = dbconn.GetRestaurantsNearLoation(30.0, 20.0);
		Set<String> nearby_businessids = new HashSet<>();
        int length = restaurants.length();
        for(int i = 0; i < length; i++){
            JSONObject restaurant = restaurants.getJSONObject(i);
            nearby_businessids.add(restaurant.getString("business_id"));  
        }  
        Set<String> expected = new HashSet<String>(Arrays.asList(
        		"--jFTZmywe7StuZ2hEjxyA", "--pOlFxITWnhzc7SHSIP0A", 
        		"--BlvDO_RG2yElKu9XA1_g", "--Y_2lDOtVDioX5bwF6GIw", 
        		"--zgHBiQpr8H2ZqSdGmguQ", "--qeSYxyn62mMjWvznNTdg", 
        		"-024YEtnIsPQCrMSHCKLQw", "--5jkZ3-nUPZxUvtcbr8Uw", 
        		"-0bl9EllYlei__4dl1W00Q", "--UE_y6auTgq3FXlvUMkbw"));
        assertEquals(expected, nearby_businessids);
		dbconn.close();
	}
}
