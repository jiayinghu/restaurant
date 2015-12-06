package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;

/**
 * Servlet implementation class SetVisitedRestaurants
 */
@WebServlet("/SetVisitedRestaurants")
public class SetVisitedRestaurants extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final DBConnection connection = new DBConnection();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetVisitedRestaurants() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				jb.append(line);
			}
			reader.close();
		} catch (Exception e) { /* report an error */
		}

		try {
			
			JSONObject input = new JSONObject(jb.toString());
			if (input.has("user_id") && input.has("visited")) {
				String user_id = (String) input.get("user_id");
				JSONArray array = (JSONArray) input.get("visited");
				List<String> visited_list = new ArrayList<>(); 
				for (int i = 0; i < array.length(); i ++) {
					String business_id = (String) array.get(i);
					visited_list.add(business_id);
				}
				connection.SetVisitedRestaurants(user_id, visited_list);
			}

			response.setContentType("application/json");
			response.addHeader("Access-Control-Allow-Origin", "*");
			PrintWriter out = response.getWriter();
			out.print("ok");
			out.flush();
			out.close();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
