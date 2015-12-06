package com.laioffer.intern_project.internproject;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiweich on 3/23/15.
 */
public class RestaurantApiClient {

    // If tested in android simulator, this must be the actual ip address of the server, not localhost
    // or 127.0.0.1
    private static final String BASE_HOSTNAME = "192.168.1.3";
    private static final String BASE_PORT = "8080";
    private static final String BASE_URL = "http://" + BASE_HOSTNAME + ":" + BASE_PORT + "/Rest/";

    private RestaurantApiClient() {}

    public static List<Restaurant> post(String[] url){
        InputStream inputStream;
        List<Restaurant> result = new ArrayList<>();
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(BASE_URL + url[0]);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("user_id", "1111");

            switch (url[0]) {
                case "GetRestaurantsNearby":
                    double lat = Double.parseDouble(url[1]);
                    double lon = Double.parseDouble(url[2]);
                    jsonObject.accumulate("lat", lat);
                    jsonObject.accumulate("lon", lon);
                    break;
                case "SetVisitedRestaurants":
                    JSONArray array = new JSONArray();
                    array.put(url[1]);
                    jsonObject.accumulate("visited", array);
                    break;
                case "RecommendRestaurants":
                    break;
            }

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                String str = convertInputStreamToString(inputStream);
                JSONArray array = new JSONArray(str);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = (JSONObject) array.get(i);
                    result.add(new Restaurant(object));
                }
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        inputStream.close();
        return result;
    }
}
