package com.example.harry.friendslist.controller;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.harry.friendslist.MainActivity;
import com.example.harry.friendslist.R;
import com.example.harry.friendslist.model.CurrentUser;
import com.example.harry.friendslist.model.Friend;
import com.example.harry.friendslist.model.Model;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

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
    private LatLng finalLatLng;
    private String[] friendsToExclude;
    private MainActivity mainActivity;

    public AsyncDistanceDemand(MainActivity mainActiviy, String[] friendsToExclude){
        this.mainActivity = mainActiviy;
        this.friendsToExclude = friendsToExclude;
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
        if(user.getLongitude().toString().equals("NaN")){
            return "failed";
        }
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

            if(userDistance == null || friendDistance == null){
                return "failed";
            }

            if((userDistance + friendDistance) < minDistance){
                minDistance = userDistance + friendDistance;
                finalDistance = userDistance;
                finalDuration = getDuration(userToMidJSON);
                finalLocation = getLocationStr(userToMidJSON);
                finalFriend = friend;
                finalLatLng = new LatLng(midLat, midLng);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return "Executed";
    }

    private JSONObject getJSON(Double fromLat, Double fromLng, Double toLat, Double toLng){

        double dist = 0.0;

        String urlString = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="
                + fromLat + "," + fromLng + "&destinations=" + toLat + "," + toLng
                + "&mode=walking&sensor=false";//key=AIzaSyAre8Beff9w66AP49w17oMxGKfIt8L3NOA";

        Log.e(LOG_TAG, urlString);

        String line;
        StringBuilder mJsonResults = new StringBuilder();

        try{
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while((line = br.readLine()) != null){
                mJsonResults.append(line);
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
            Log.e(LOG_TAG, jsonObject.toString());
            return jsonObject;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON");
            return null;
        }
    }

    private String getDuration(JSONObject jsonObject){
        try{

            JSONObject duration = jsonObject.getJSONArray("rows")
                    .getJSONObject(0).getJSONArray("elements")
                    .getJSONObject(0).getJSONObject("duration");
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
            JSONObject distance = jsonObject.getJSONArray("rows")
                    .getJSONObject(0).getJSONArray("elements")
                    .getJSONObject(0).getJSONObject("distance");
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
            JSONArray location = jsonObject.getJSONArray("destination_addresses");
            locationStr = location.getString(0);
            return locationStr;

        } catch (JSONException e){
            Log.e(LOG_TAG, "Error parsing json");
            return null;
        }
    }

    protected void onPostExecute(String result){
        String[] nFriendsToExclude;
        if(result.equals("failed")){
            mainActivity.failedDistance();
            return;
        }
        if(friendsToExclude == null){
            friendsToExclude = new String[1];
            friendsToExclude[0] = finalFriend.getId();
            mainActivity.onDistanceCalculated(finalDuration,finalDistance,finalFriend, friendsToExclude, finalLocation, finalLatLng);
        } else {
            nFriendsToExclude = new String[friendsToExclude.length + 1];
            int count = 0;
            for(int j = 0; j < friendsToExclude.length; j++){
                nFriendsToExclude[j] = friendsToExclude[j];
                count++;
            }
            nFriendsToExclude[count] = finalFriend.getId();
            mainActivity.onDistanceCalculated(finalDuration,finalDistance,finalFriend, nFriendsToExclude, finalLocation, finalLatLng);
        }
    }
}
