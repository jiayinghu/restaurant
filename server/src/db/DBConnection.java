package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import yelp.Restaurant;
import yelp.YelpAPI;

public class DBConnection {
	private Connection conn = null;
	private static final int MAX_RECOMMENDED_RESTAURANTS = 10;
	private static final int MIN_RECOMMENDED_RESTAURANTS = 3;
	/**
	 * Make sure it is the only place to configure db related parameters
	 */
	public static final String HOSTNAME = "localhost";
	public static final String PORT = "3306";
	public static final String DBNAME = "mysql";
	public static final String USERNAME = "root";
	public static final String PASSWORD = "root";
	public static final String URL;
		
	static {
		URL = "jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + DBNAME
				+ "?user=" + USERNAME +"&password=" + PASSWORD;
	}
	
	public DBConnection() {
		this(URL);
	}

	public DBConnection(String url) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close(){
	    if (conn != null) {
	        try {
	        	conn.close();
	        } catch (Exception e) { /* ignored */}
	    }
	}

	public void SetVisitedRestaurants(String userId, List<String> businessIds) {
		try {
			if (conn == null) {
				return;
			}
			Statement stmt = conn.createStatement();
			String sql = "";
			for (String businessId : businessIds) {
				sql = "INSERT INTO USER_VISIT_HISTORY (`user_id`, `business_id`) VALUES (\""
						+ userId + "\", \"" + businessId + "\")";
				stmt.executeUpdate(sql);
			}

		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
	}

	public void UnsetVisitedRestaurants(String userId, List<String> businessIds) {
		try {
			if (conn == null) {
				return;
			}
			Statement stmt = conn.createStatement();
			String sql = "";
			for (String businessId : businessIds) {
				sql = "DELETE FROM USER_VISIT_HISTORY WHERE `user_id`=\"" + userId + "\" and `business_id` = \""
						+ businessId + "\"";
				stmt.executeUpdate(sql);
			}

		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
	}

	private Set<String> getCategories(String business_id) {
		try {
			if (conn == null) {
				return null;
			}
			Statement stmt = conn.createStatement();
			String sql = "SELECT categories from RESTAURANTS WHERE business_id='"
					+ business_id + "'";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				Set<String> set = new HashSet<>();
				String[] categories = rs.getString("categories").split(",");
				for (String category : categories) {
					// ' Japanese ' -> 'Japanese'
					set.add(category.trim());
				}
				return set;
			}
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
		return new HashSet<String>();
	}

	private Set<String> getBusinessId(String category) {
		Set<String> set = new HashSet<>();
		try {
			if (conn == null) {
				return null;
			}
			Statement stmt = conn.createStatement();
			// if category = Chinese, categories = Chinese, Korean, Japanese, it's a match
			String sql = "SELECT business_id from RESTAURANTS WHERE categories LIKE '%"
					+ category + "%'";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String business_id = rs.getString("business_id");
				set.add(business_id);
			}
			return set;
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
		return set;
	}

	public Set<String> getVisitedRestaurants(String userId) {
		Set<String> visitedRestaurants = new HashSet<String>();
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT business_id from USER_VISIT_HISTORY WHERE user_id="
					+ userId;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String visited_restaurant = rs.getString("business_id");
				visitedRestaurants.add(visited_restaurant);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return visitedRestaurants;
	}

	private JSONObject getRestaurantsById(String businessId) {
		try {
			Statement stmt = conn.createStatement();
			String sql = "SELECT business_id, name, full_address, categories, stars, latitude, longitude, city, state, image_url from "
					+ "RESTAURANTS where business_id='" + businessId + "'" + " ORDER BY stars DESC";
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("business_id", rs.getString("business_id"));
				obj.put("name", rs.getString("name"));
				obj.put("stars", rs.getFloat("stars"));
				obj.put("latitude", rs.getFloat("latitude"));
				obj.put("longitude", rs.getFloat("longitude"));
				obj.put("full_address", rs.getString("full_address"));
				obj.put("city", rs.getString("city"));
				obj.put("state", rs.getString("state"));
				obj.put("categories",
						DBImport.stringToJSONArray(rs.getString("categories")));
				obj.put("image_url", rs.getString("image_url"));
				return obj;
			}
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	private Set<String> getMoreCategories(String category, int maxCount) {
		Set<String> allCategories = new HashSet<>();
		if (conn == null) {
			return null;
		}
		Statement stmt;
		try {
			stmt = conn.createStatement();

			String sql = "SELECT second_id from USER_CATEGORY_HISTORY WHERE first_id=\""
					+ category + "\" ORDER BY count DESC LIMIT " + maxCount;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String visited_restaurant = rs.getString("second_id");
				allCategories.add(visited_restaurant);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allCategories;
	}
	
	private Set<String> getMoreCategories(Set<String> existingCategories) {
		Set<String> allCategories = new HashSet<>();
		for (String category : existingCategories) {
			allCategories.addAll(getMoreCategories(category, 5));
		}
		return allCategories;
	}

	public JSONArray RecommendRestaurants(String userId) {
		try {
			if (conn == null) {
				return null;
			}

			Set<String> visitedRestaurants = getVisitedRestaurants(userId);
			Set<String> allCategories = new HashSet<>();// why hashSet?
			for (String restaurant : visitedRestaurants) {
				allCategories.addAll(getCategories(restaurant));
			}
			Set<String> allRestaurants = new HashSet<>();
			for (String category : allCategories) {
				Set<String> set = getBusinessId(category);
				allRestaurants.addAll(set);
			}
			Set<JSONObject> diff = new HashSet<>();
			int count = 0;
			for (String business_id : allRestaurants) {
				// Perform filtering
				if (!visitedRestaurants.contains(business_id)) {
					diff.add(getRestaurantsById(business_id));
					count++;
					if (count >= MAX_RECOMMENDED_RESTAURANTS) {
						break;
					}
				}
			}
			
			if (count < MIN_RECOMMENDED_RESTAURANTS) {
				allCategories.addAll(getMoreCategories(allCategories));
				for (String category : allCategories) {
					Set<String> set = getBusinessId(category);
					allRestaurants.addAll(set);
				}
				for (String business_id : allRestaurants) {
					if (!visitedRestaurants.contains(business_id)) {
						diff.add(getRestaurantsById(business_id));
						count++;
						if (count >= MAX_RECOMMENDED_RESTAURANTS) {
							break;
						}
					}
				}
			}		
			
			return new JSONArray(diff);
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
		return null;
	}

	public JSONArray GetRestaurantsNearLoation(double lat, double lon) {
		try {
			if (conn == null) {
				return null;
			}
			Statement stmt = conn.createStatement();
			String sql = "SELECT business_id, name, full_address, categories, stars, latitude, longitude, city, state from RESTAURANTS LIMIT 10";
			ResultSet rs = stmt.executeQuery(sql);
			List<JSONObject> list = new ArrayList<JSONObject>();
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				obj.put("business_id", rs.getString("business_id"));
				obj.put("name", rs.getString("name"));
				obj.put("stars", rs.getFloat("stars"));
				obj.put("latitude", rs.getFloat("latitude"));
				obj.put("longitude", rs.getFloat("longitude"));
				obj.put("full_address", rs.getString("full_address"));
				obj.put("city", rs.getString("city"));
				obj.put("state", rs.getString("state"));
				obj.put("categories",
						DBImport.stringToJSONArray(rs.getString("categories")));
				list.add(obj);
			}
			return new JSONArray(list);
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
		return null;
	}

	public JSONArray GetRestaurantsNearLoationViaYelpAPI(double lat, double lon) {
		try {
			YelpAPI api = new YelpAPI();
			JSONObject response = new JSONObject(
					api.searchForBusinessesByLocation(lat, lon));
			JSONArray array = (JSONArray) response.get("businesses");
			if (conn == null) {
				return null;
			}
			Statement stmt = conn.createStatement();
			String sql = "";
			List<JSONObject> list = new ArrayList<JSONObject>();

			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				Restaurant restaurant = new Restaurant(object);
				String business_id = restaurant.getBusinessId();
				String name = restaurant.getName();
				String categories = restaurant.getCategories();
				String city = restaurant.getCity();
				String state = restaurant.getState();
				String fullAddress = restaurant.getFullAddress();
				double stars = restaurant.getStars();
				double latitude = restaurant.getLatitude();
				double longitude = restaurant.getLongitude();
				String imageUrl = restaurant.getImageUrl();
				JSONObject obj = new JSONObject();
				obj.put("business_id", business_id);
				obj.put("name", name);
				obj.put("stars", stars);
				obj.put("latitude", latitude);
				obj.put("longitude", longitude);
				obj.put("full_address", fullAddress);
				obj.put("city", city);
				obj.put("state", state);
				obj.put("categories", categories);
				obj.put("image_url", imageUrl);
				sql = "INSERT IGNORE INTO RESTAURANTS " + "VALUES ('"
						+ business_id + "', \"" + name + "\", \"" + categories
						+ "\", '" + city + "', '" + state + "', " + stars
						+ ", \"" + fullAddress + "\", " + latitude + ","
						+ longitude + ",\"" + imageUrl + "\")";
				System.out.println(sql);
				stmt.executeUpdate(sql);
				list.add(obj);
			}
			return new JSONArray(list);
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public static void main(String[] args) {
		//This is for test purpose
		DBConnection conn = new DBConnection("jdbc:mysql://localhost:3306/mysql?user=root&password=root");
		JSONArray array = conn.GetRestaurantsNearLoationViaYelpAPI(1.0, 2.0);
	}
}
