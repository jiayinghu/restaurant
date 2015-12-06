package com.laioffer.intern_project.internproject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListActivity extends Activity {

    public final static String RESTAURANT_LOC = "com.laioffer.internproject.RESTAURANT_LOC";

    private RestaurantListFragment listFragment;
    private RestaurantMapFragment mapFragment;
    private LocationManager mManager;
    private MyLocationListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_restaurant_list);
        listFragment = (RestaurantListFragment) getFragmentManager().findFragmentByTag("list_frag");
        mapFragment = (RestaurantMapFragment) getFragmentManager().findFragmentByTag("map_frag");
        mManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mListener = new MyLocationListener();
        mManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mListener);
    }

    /**
     * Use this method to instantiate the action bar, and add your items to it.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant_list, menu);

        // It should return true if you have added items to it and want the menu
        // to be displayed.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            // Implement the action here!
            GetRestaurantsNearbyAsyncTask task = new GetRestaurantsNearbyAsyncTask();
            task.execute("GetRestaurantsNearby", Double.toString(mListener.getLat()), Double.toString(mListener.getLon()));
            listFragment.getRestaurantListAdapter().notifyDataSetChanged();
            return true;
        } else if (id == R.id.action_location) {
            switchToMapView(restaurant_loc);
        } else if (id == R.id.action_recommend) {
            recommendRestaurants();
        }

        return super.onOptionsItemSelected(item);
    }

    private void recommendRestaurants() {
        // It is asynchronously updating the result so as not to block UI thread
        RecommendRestaurantsAsyncTask t = new RecommendRestaurantsAsyncTask();
        t.execute("RecommendRestaurants");
    }

    // Create the intent and switch to map view activity.
    private void switchToMapView(ArrayList<LatLng> restaurant_loc) {
        Intent intent = new Intent(this, RestaurantMapActivity.class);
        restaurant_loc.clear();
        for (Restaurant restaurant : listFragment.getRestaurantListAdapter().getRestaurants()) {
            restaurant_loc.add(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()));
        }

        intent.putParcelableArrayListExtra(RESTAURANT_LOC, restaurant_loc);
        startActivity(intent);
    }


    private void updateRestaurantsInMap() {

    }

    // Store the locations of all restaurants.
    private ArrayList<LatLng> restaurant_loc = new ArrayList<>();

    class GetRestaurantsNearbyAsyncTask extends AsyncTask<String, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(String... params) {
            return RestaurantApiClient.post(params);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            super.onPostExecute(restaurants);
            listFragment.getRestaurantListAdapter().updateRestaurants(restaurants);
            listFragment.getRestaurantListAdapter().notifyDataSetChanged();
        }
    }

    private class RecommendRestaurantsAsyncTask extends AsyncTask<String, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(String... params) {
            return RestaurantApiClient.post(params);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            super.onPostExecute(restaurants);
            listFragment.getRestaurantListAdapter().updateRestaurants(restaurants);
            listFragment.getRestaurantListAdapter().notifyDataSetChanged();
        }
    }
}
