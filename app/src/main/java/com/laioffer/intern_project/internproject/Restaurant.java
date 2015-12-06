package com.laioffer.intern_project.internproject;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by weiweich on 3/21/15.
 */
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

    public Restaurant(JSONObject object) {
        if (object != null) {
            try {
                this.businessId = ((JSONArray)object.get("business_id")).getString(0);
                this.categories = "";//(JSONArray)object.get("categories");
                this.name = ((JSONArray)object.get("name")).getString(0);
                this.city = ((JSONArray)object.get("city")).getString(0);
                this.state = ((JSONArray)object.get("state")).getString(0);
                this.stars = ((JSONArray)object.get("stars")).getDouble(0);
                this.fullAddress = ((JSONArray)object.get("full_address")).getString(0);
                this.latitude = ((JSONArray)object.get("latitude")).getDouble(0);
                this.longitude = ((JSONArray)object.get("longitude")).getDouble(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Restaurant(String businessId,
                      String name,
                      String categories,
                      String city,
                      String state,
                      double stars,
                      String fullAddress,
                      double latitude,
                      double longitude) {
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

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
 }