package db;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBExport {
	private static Connection conn = null;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager
					.getConnection("jdbc:mysql://localhost:3306/mysql?"
							+ "user=root&password=root");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			if (conn == null) {
				return;
			}
			Statement stmt = conn.createStatement();
			String sql = "SELECT categories FROM RESTAURANTS AS T1 JOIN USER_REVIEW_HISTORY AS T2 ON T1.business_id = T2.business_id;";
			ResultSet rs = stmt.executeQuery(sql);
			BufferedWriter writer = new BufferedWriter(new FileWriter(
					"../dataset/review_data"));
			
			while (rs.next()) {
				String categories = rs.getString("categories");
				writer.write(categories + "\n");
			}
			writer.flush();
			writer.close();
		} catch (Exception e) { /* report an error */
			System.out.println(e.getMessage());
		}
	}
}
