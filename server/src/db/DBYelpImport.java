package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;

public class DBYelpImport {

	public static String parseString(String str) {
		return str.replace("\"", "\\\"").replace("/", " or ");
	}

	public static String jsonArrayToString(JSONArray array) {
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < array.length(); i++) {
				String obj = (String) array.get(i);
				sb.append(obj);
				if (i != array.length() - 1) {
					sb.append(",");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static JSONArray stringToJSONArray(String str) {
		try {
			return new JSONArray("[" + str + "]");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection conn = null;

			try {
				System.out.println("Connecting to \n" + DBConnection.URL);
				conn = DriverManager.getConnection(DBConnection.URL);
			} catch (SQLException e) {
				System.out.println("SQLException " + e.getMessage());
				System.out.println("SQLState " + e.getSQLState());
				System.out.println("VendorError " + e.getErrorCode());
			}
			if (conn == null) {
				return;
			}
			Statement stmt = conn.createStatement();
			
			String sql = "DROP TABLE IF EXISTS USER_VISIT_HISTORY";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS RESTAURANTS";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE RESTAURANTS "
					+ "(business_id VARCHAR(255) NOT NULL, "
					+ " name VARCHAR(255), " + "categories VARCHAR(255), "
					+ "city VARCHAR(255), " + "state VARCHAR(255), "
					+ "stars FLOAT," + "full_address VARCHAR(255), "
					+ "latitude FLOAT, " + " longitude FLOAT, "
					+ "image_url VARCHAR(255),"
					+ " PRIMARY KEY ( business_id ))";
			stmt.executeUpdate(sql);

			sql = "DROP TABLE IF EXISTS USERS";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE USERS "
					+ "(user_id VARCHAR(255) NOT NULL, "
					+ " first_name VARCHAR(255), last_name VARCHAR(255), "
					+ " PRIMARY KEY ( user_id ))";
			stmt.executeUpdate(sql);
			sql = "INSERT INTO USERS " + "VALUES (\"1111\", \"John\", \"Smith\")";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE USER_VISIT_HISTORY "
					+ "(visit_history_id bigint(20) unsigned NOT NULL AUTO_INCREMENT, "
					+ " user_id VARCHAR(255) NOT NULL , "
					+ " business_id VARCHAR(255) NOT NULL, " 
					+ " last_visited_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, "
					+ " PRIMARY KEY (visit_history_id),"
					+ "FOREIGN KEY (business_id) REFERENCES RESTAURANTS(business_id),"
					+ "FOREIGN KEY (user_id) REFERENCES USERS(user_id))";
			stmt.executeUpdate(sql);
			
			sql = "DROP TABLE IF EXISTS USER_CATEGORY_HISTORY";
			stmt.executeUpdate(sql);
			
			sql = "CREATE TABLE USER_CATEGORY_HISTORY "
					+ "(category_id bigint(20) unsigned NOT NULL AUTO_INCREMENT, "
					+ " first_id VARCHAR(255) NOT NULL , "
					+ " second_id VARCHAR(255) NOT NULL, "
					+ " count bigint(20) NOT NULL, "
					+ " PRIMARY KEY (category_id))";
			stmt.executeUpdate(sql);

			System.out.println("Done Importing");

		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
	}
}
