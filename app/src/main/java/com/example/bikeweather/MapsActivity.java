package com.example.bikeweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.argb;


class threehrforecast{
    public float lat;
    public float lon;
    public String timezone;
    public int timeOffSet;
    public Object[] minutely;
}


public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback {


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Connect to the map
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // initialize map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("some text"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));




        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }


    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        Intent switchActivityIntent = new Intent(this, enterinfo.class);
        startActivityForResult(switchActivityIntent, 1000);
    }

    // unix timestamp formatter
    public String timeGen(int hour, int minute, int year, int month, int day){
        String output = "";
        String strHour = String.valueOf(hour);
        String strMin = String.valueOf(minute);
        String strYr = String.valueOf(year);
        String strMon = String.valueOf(month);
        String strDay = String.valueOf(day);
        if(strHour.length()==1){
            strHour = "0"+strHour;
        }
        if(strMin.length()==1){
            strMin = "0"+strMin;
        }
        if(strMon.length()==1){
            strMon = "0"+strMon;
        }
        if(strDay.length()==1) {
            strDay = "0" + strDay;
        }

        // convert to yyyy-MM-dd HH:mm:ss format
        // need to account for 0x case
        output = output + strYr+"/"+strMon+"/"+strDay+"%20"+strHour+":"+strMin+":00";
        return output;
    }
    // distance function to decide whether or not to draw circle
    public double distanceFrom(double x1, double x2, double y1, double y2){
        double distx = x2-x1;
        double disty = y2-y1;

        double dist = Math.sqrt(Math.pow(distx,2)+Math.pow(disty,2));

        return dist;
    }

    // generate latlng points given radius and location
    public List<LatLng> weatherpoints(double lat, double lon, int rad){
        List<LatLng> thepoints = new ArrayList<LatLng>();
        double diff = rad/10;
        for(int i = -10;i<10;i++){
            for(int j = -10;j<10;j++){
                double xcoord = i*diff/100000 + lat;
                double ycoord = j*diff/100000 + lon;
                double length = distanceFrom(lat, xcoord, lon, ycoord);
                if((length*111139)<rad){
                    LatLng point = new LatLng(xcoord,ycoord);
                    thepoints.add(point);
                }
            }
        }
        return thepoints;
    }
    public interface VolleyCallback{
        void onSuccess(String result);
    }
    public void getunixtime(String url, final VolleyCallback callback){
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.

        final String []results = new String[1];
        results[0]="1";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        results[0] = response.substring(1,response.length()-1);
                        callback.onSuccess(results[0]);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        results[0]="error";

                        Log.i("resultsTry", results[0]);

                    }
                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void getweather(String time, String url, final VolleyCallback callback){
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("oncalljson",response);
                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("resultsTry", "error");

            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String date = data.getStringExtra("date");
        String location = data.getStringExtra("location");
        String time = data.getStringExtra("time");
        String radius = data.getStringExtra("radius");

        String[] timeSplit = time.split(":",2);
        String[] dateSplit = date.split("/", 3);
        String[] placeSplit = location.split("/", 2);
        int area = Integer.parseInt(radius);
        int hour = Integer.parseInt(timeSplit[0]);
        int minute = Integer.parseInt(timeSplit[1]);
        int year = Integer.parseInt(dateSplit[0]);
        int month = Integer.parseInt(dateSplit[1]);
        int day = Integer.parseInt(dateSplit[2]);
        double latitude = Double.parseDouble(placeSplit[0]);
        double longitude = Double.parseDouble(placeSplit[1]);
        // convert time format
        //String formatTime = timeGen(hour, minute, year, month, day);

        // get the points for drawing circles
        List<LatLng> thepoints = weatherpoints(latitude,longitude,area);


        // get and organize data for drawing circles
        ArrayList<threehrforecast> grabpoints = new ArrayList<threehrforecast>();
        for(int i =0;i<thepoints.size();i++){
            double lat = thepoints.get(i).latitude;
            double lon = thepoints.get(i).longitude;
            String weatherurl = "https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude=current,hourly,daily&appid=ced066415607aa1e214b187481c06567";
            getweather("unixt", weatherurl, new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.i("print initial json", result);
                    Gson g = new Gson();
                    threehrforecast minuterain = g.fromJson(result, threehrforecast.class);
                    grabpoints.add(minuterain);
                    LatLng point = new LatLng(latitude,longitude);
                    if(minuterain.minutely!=null){
                        LinkedTreeMap<String, Object> testing = (LinkedTreeMap<String, Object>)minuterain.minutely[0];
                        Log.i("try", testing.get("precipitation").toString());
                        int transparent = (int) Math.round((Double) testing.get("precipitation"));

                    }
                    else{
                        Log.i("it was ","null");
                    }
                }
            });
/*CircleOptions weathercircle = new CircleOptions()
                                .center(point)
                                .radius(area/10) // In meters
                                .fillColor(argb(transparent,50, 149, 168))
                                .strokeColor(argb(transparent,50, 149, 168));
                        mMap.addCircle(weathercircle);*/


        }

        // move to location
        LatLng bikingspot = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bikingspot));
    }
}
