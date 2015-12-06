package db;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class DBConnectionTest {


	@Test
	public void testVisitedRestaurants() {
		DBConnection dbconn = new DBConnection("jdbc:mysql://localhost:3306/unittest?user=root&password=root");
		String userId = "1111";
		// Remove the existing visited data from DB
		Set<String> original_visited = dbconn.getVisitedRestaurants(userId);
		dbconn.UnsetVisitedRestaurants(userId,new ArrayList<String>(original_visited));
		
		// Check if the UnsetVisitedRestaurants works
		Set<String> visited = dbconn.getVisitedRestaurants(userId);
		assertTrue(visited.isEmpty());
		
		// Check if the SetVisitedRestaurants works
		List<String> businessIds = Arrays.asList("--qeSYxyn62mMjWvznNTdg");
		dbconn.SetVisitedRestaurants(userId, businessIds);
		visited = dbconn.getVisitedRestaurants(userId);
		for (String businessId : businessIds) {
			assertTrue(visited.contains(businessId));
		}
		
		// Restore the original visited data
		dbconn.SetVisitedRestaurants(userId, new ArrayList<String>(original_visited));
		dbconn.close();
	}

}
