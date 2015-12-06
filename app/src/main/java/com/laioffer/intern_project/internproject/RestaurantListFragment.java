package com.laioffer.intern_project.internproject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class RestaurantListFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;
    public RestaurantListAdapter adapter;

    // TODO: Rename and change types of parameters
    public static RestaurantListFragment newInstance(String param1, String param2) {
        RestaurantListFragment fragment = new RestaurantListFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RestaurantListFragment() {

    }

    public RestaurantListAdapter getRestaurantListAdapter() {
        return this.adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the ListAdapter.
        adapter = new RestaurantListAdapter(getActivity(), new ArrayList<Restaurant>());
        GetRestaurantsNearbyAsyncTask task = new GetRestaurantsNearbyAsyncTask();
        task.execute("GetRestaurantsNearby");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Restaurant restaurant = adapter.restaurants.get(position);
        SetVisitedRestaurantsAsyncTask task = new SetVisitedRestaurantsAsyncTask();
        task.execute("SetVisitedRestaurants", restaurant.getBusinessId());
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            // mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    class RestaurantListAdapter extends ArrayAdapter<Restaurant> {

        private List<Restaurant> restaurants;
        private Context context;
        RestaurantListAdapter(Context context, List<Restaurant> restaurants) {
            super(getActivity(), R.layout.fragment_restaurant_list_item, R.id.restaurant_name,
                    restaurants);
            this.restaurants = restaurants;
            this.context = context;
        }
        private class ViewHolder {
            TextView titleText;
        }

        public void updateRestaurants(List<Restaurant> restaurants) {
            this.restaurants.clear();
            this.restaurants.addAll(restaurants);
        }

        public List<Restaurant> getRestaurants() {
            return this.restaurants;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Restaurant restaurant = getItem(position);
            View viewToUse;

            // This block exists to inflate the settings list item conditionally based on whether
            // we want to support a grid or list view.
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                viewToUse = mInflater.inflate(R.layout.fragment_restaurant_list_item,  null);
                holder = new ViewHolder();
                holder.titleText = (TextView)viewToUse.findViewById(R.id.restaurant_name);
                viewToUse.setTag(holder);
            } else {
                viewToUse = convertView;
                holder = (ViewHolder) viewToUse.getTag();
            }

            holder.titleText.setText(restaurant.getName());
            return viewToUse;
        }
    }

    class GetRestaurantsNearbyAsyncTask extends AsyncTask<String, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(String... params) {
            return RestaurantApiClient.post(params);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            super.onPostExecute(restaurants);
            adapter.updateRestaurants(restaurants);
            setListAdapter(adapter);
        }
    }

    private class SetVisitedRestaurantsAsyncTask extends AsyncTask<String, Void, List<Restaurant>> {

        @Override
        protected List<Restaurant> doInBackground(String... params) {
            return RestaurantApiClient.post(params);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(List<Restaurant> restaurants) {
            super.onPostExecute(restaurants);
        }
    }
}
