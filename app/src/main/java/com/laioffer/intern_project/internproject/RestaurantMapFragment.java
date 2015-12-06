package com.laioffer.intern_project.internproject;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapFragment;


/**
 * A simple {@link com.google.android.gms.maps.MapFragment} subclass.
 */
public class RestaurantMapFragment extends MapFragment {


    public RestaurantMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_map, container, false);
    }


}
