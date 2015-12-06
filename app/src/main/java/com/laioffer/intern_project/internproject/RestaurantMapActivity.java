package com.laioffer.intern_project.internproject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * Shows all restaurants on a map.
 */
public class RestaurantMapActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_map);

        // Set up the map.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(
                        R.id.restaurant_map);
        mapFragment.getMapAsync(this);

        // Extract restaurant locations from intent.
        Intent intent = getIntent();
        restaurant_loc =
                intent.getParcelableArrayListExtra(RestaurantListActivity.RESTAURANT_LOC);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        for (LatLng loc : restaurant_loc) {
            map.addMarker(new MarkerOptions().position(loc).title("Marker"));
        }
        if (!restaurant_loc.isEmpty()) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurant_loc.get(0), 9));
        }
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);

    }

    // Store the locations of all restaurants.
    private ArrayList<LatLng> restaurant_loc;
}
