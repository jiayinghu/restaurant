package yelp;

import org.json.JSONArray;
import org.json.JSONObject;

import db.DBImport;

public class Restaurant {
	private String businessId;
	private String name;
	private String categories;
	private String city;
	private String state;
	private String fullAddress;
	private double stars;
	private double latitude;
	private double longitude;
	private String imageUrl;

	public Restaurant(JSONObject object) {
		try {
			if (object != null) {
				this.businessId = object.getString("id");
				JSONArray array = (JSONArray) object.get("categories");
				this.categories = DBImport.jsonArrayToString((JSONArray)array.get(0));
				this.name = object.getString("name");
				this.imageUrl = object.getString("image_url");//either image_url (big) or mobile_url(small)
				this.stars = object.getDouble("rating");
				JSONObject location = (JSONObject) object.get("location");
				JSONObject coordinate = (JSONObject) location.get("coordinate");
				this.latitude = coordinate.getDouble("latitude");
				this.longitude = coordinate.getDouble("longitude");
				this.city = location.getString("city");
				this.state = location.getString("state_code");
				this.fullAddress = ((JSONArray) location.get("display_address")).get(0).toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Restaurant(String businessId, String name, String categories,
			String city, String state, double stars, String fullAddress,
			double latitude, double longitude) {
		this.businessId = businessId;
		this.categories = categories;
		this.name = name;
		this.city = city;
		this.state = state;
		this.stars = stars;
		this.fullAddress = fullAddress;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getBusinessId() {
		return this.businessId;
	}

	public String getName() {
		return this.name;
	}

	public String getCategories() {
		return this.categories;
	}

	public String getCity() {
		return this.city;
	}

	public String getState() {
		return this.state;
	}

	public String getFullAddress() {
		return this.fullAddress;
	}

	public double getStars() {
		return this.stars;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}
	
	public String getImageUrl() {
		return this.imageUrl;
	}
}
