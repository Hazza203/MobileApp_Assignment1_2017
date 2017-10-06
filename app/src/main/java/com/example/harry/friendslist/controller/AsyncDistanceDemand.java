package com.example.harry.friendslist.controller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.harry.friendslist.MainActivity;
import com.example.harry.friendslist.model.CurrentUser;
import com.example.harry.friendslist.model.Friend;
import com.example.harry.friendslist.model.Model;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by harry on 6/10/2017.
 */

public class AsyncDistanceDemand extends AsyncTask<String, Void, String> {

    private String LOG_TAG = this.getClass().getName();
    private String finalDuration, finalLocation;
    private Model model;
    private CurrentUser user;
    private List<Friend> friendList;
    private Double minDistance = 9999999.0;
    private Double finalDistance;
    private Friend finalFriend;
    private String[] friendsToExclude;
    private MainActivity mainActivity;
    private ProgressDialog progressDialog;

    public AsyncDistanceDemand(MainActivity mainActiviy, String[] friendsToExclude){
        this.mainActivity = mainActiviy;
        this.friendsToExclude = friendsToExclude;
        progressDialog = new ProgressDialog(mainActivity);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Finding closest match...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {

        Double distanceTemp, lat, lng, midLat, midLng;
        Double friendLat, friendLng;
        int count = 0;

        model = Model.getInstance();
        user = model.getCurrentUser();
        lat = user.getLatitude();
        lng = user.getLongitude();
        friendList = user.getFriendsList();

        outer:
        for(Friend friend: friendList) {
            if(friendsToExclude != null){
                for (int i = 0; i < friendsToExclude.length; i++) {
                    if (friend.getId().equals(friendsToExclude[i])) {
                        continue outer;
                    }
                }
            }

            friendLat = friend.getLatitude();
            friendLng = friend.getLongitude();

            //Calculating the mid point
            midLat = (friendLat + lat) / 2;
            midLng = (friendLng + lng) / 2;

            Log.e(LOG_TAG, "Friend details" + friendLat.toString() + friendLng.toString());
            Log.e(LOG_TAG, "User details" + lat.toString() + lng.toString());
            Log.e(LOG_TAG, "Mid details" + midLat.toString() + midLng.toString());

            //Getting user walk distance/time
            JSONObject userToMidJSON = getJSON(lat, lng, midLat, midLng);
            JSONObject friendToMidJSON = getJSON(friendLat, friendLng, midLat, midLng);
            Double userDistance = getDistance(userToMidJSON);
            Double friendDistance = getDistance(friendToMidJSON);

            if((userDistance + friendDistance) < minDistance){
                minDistance = userDistance + friendDistance;
                finalDistance = userDistance;
                finalDuration = getDuration(userToMidJSON);
                finalLocation = getLocationStr(userToMidJSON);
                finalFriend = friend;
            }
        }

        return "Executed";
    }

    private JSONObject getJSON(Double fromLat, Double fromLng, Double toLat, Double toLng){

        double dist = 0.0;

        String urlString = "http://maps.googleapis.com/maps/api/directions/json?origin="
                + fromLat + "," + fromLng + "&destination=" + toLat + "," + toLng
                + "&mode=walking&sensor=false";
        Log.e(LOG_TAG, urlString);

        StringBuilder mJsonResults = new StringBuilder();

        try{
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
            int b;
            while ((b = in.read()) != -1) {
                mJsonResults.append((char) b);
            }
        }  catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Distance Matrix API URL");
            return null;

        }catch (IOException e) {
            Log.e(LOG_TAG, "IO Error connecting to Distance Matrix API");
            return null;
        }

        try {
            JSONObject jsonObject = new JSONObject(mJsonResults.toString());
            return jsonObject;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON");
            return null;
        }
    }

    private String getDuration(JSONObject jsonObject){
        try{

            JSONArray array = jsonObject.getJSONArray("routes");
            JSONObject routes = array.getJSONObject(0);
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject steps = legs.getJSONObject(0);
            JSONObject duration = steps.getJSONObject("duration");
            String durationStr = duration.getString("text");
            return durationStr;

        } catch (JSONException e){
            Log.e(LOG_TAG, "Error parsing json");
            return null;
        }
    }

    private Double getDistance(JSONObject jsonObject){
        Double dist;
        try{
            JSONArray array = jsonObject.getJSONArray("routes");
            JSONObject routes = array.getJSONObject(0);
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject steps = legs.getJSONObject(0);
            JSONObject distance = steps.getJSONObject("distance");

            dist = Double.parseDouble(distance.getString("text").replaceAll("[^\\.0123456789]",""));
            return dist;

        } catch (JSONException e){
            Log.e(LOG_TAG, "Error parsing json");
            return null;
        }
    }

    private String getLocationStr(JSONObject jsonObject){
        String locationStr;
        try{
            JSONArray array = jsonObject.getJSONArray("routes");
            JSONObject routes = array.getJSONObject(0);
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject steps = legs.getJSONObject(0);
            locationStr = steps.getString("end_address");
            return locationStr;

        } catch (JSONException e){
            Log.e(LOG_TAG, "Error parsing json");
            return null;
        }
    }

    protected void onPostExecute(String result){
        progressDialog.dismiss();
        mainActivity.onDistanceCalculated(finalDuration,finalDistance,finalFriend, finalLocation);
    }
}
