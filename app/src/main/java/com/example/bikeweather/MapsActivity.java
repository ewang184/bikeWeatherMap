package com.example.bikeweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

    List<LatLng> routepoints = new ArrayList<LatLng>();
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

        // if route list is not empty draw a line
        if(!routepoints.isEmpty()){
            Polyline route = mMap.addPolyline(new PolylineOptions()
                    .add(
                            routepoints.get(routepoints.size()-1),
                            latLng
                    )
            );
        }

        routepoints.add(latLng);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //mMap.clear();
        Intent switchActivityIntent = new Intent(this, enterinfo.class);
        startActivityForResult(switchActivityIntent, 1000);
    }
    public double distanceFrom(double x1, double x2, double y1, double y2){
        double distx = x2-x1;
        double disty = y2-y1;

        double dist = Math.sqrt(Math.pow(distx,2)+Math.pow(disty,2));

        return dist;
    }
    // function to find center-point of route
    public LatLng centerroute(List<LatLng> route){
        double latAve = 0.0;
        double lonAve = 0.0;
        for(int i = 0;i<route.size();i++){
            latAve += route.get(i).latitude;
            lonAve += route.get(i).longitude;
        }
        latAve = latAve/route.size();
        lonAve = lonAve/route.size();
        LatLng returnpoint = new LatLng(latAve,lonAve);
        return returnpoint;
    }
    // function to find radius of route
    public double centerrad(List<LatLng> route, LatLng center){
        //find furthest point and multiply by sqrt2
        double rad = 0.0;
        double midx = center.latitude;
        double midy = center.longitude;

        for(int i = 0; i < route.size(); i++){
            double dist = distanceFrom(midx*111139,route.get(i).latitude*111139,midy*111139,route.get(i).longitude*111139);
            if(dist>rad){
                rad = dist;
            }
        }
        return rad;
    }
    // distance function to decide whether or not to draw circle


    // generate latlng points given radius and location
    public List<LatLng> weatherpoints(double lat, double lon, double rad){
        List<LatLng> thepoints = new ArrayList<LatLng>();
        double diff = rad/2.5;
        for(double i = -2.5;i<2.5;i++){
            for(double j = -2.5;j<2.5;j++){
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


    public void getweather(String time, String url, final VolleyCallback callback){
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
        if (resultCode==1000) {
            LatLng center = centerroute(routepoints);
            double span = centerrad(routepoints, center);

            String minahead = data.getStringExtra("time");

            int foreMin = Integer.parseInt(minahead);
            Log.i("center", String.valueOf(center));
            Log.i("span", String.valueOf(span));
            // get the points for drawing circles
            List<LatLng> thepoints = weatherpoints(center.latitude, center.longitude, span);
            CircleOptions drawcircle = new CircleOptions()
                    .center(center)
                    .radius(span) // In meters
                    .fillColor(argb(100, 235, 0, 0))
                    .strokeColor(argb(100, 235, 0, 0));
            mMap.addCircle(drawcircle);


            // get and organize data for drawing circles
            for (int i = 0; i < thepoints.size(); i++) {
                double lat = thepoints.get(i).latitude;
                double lon = thepoints.get(i).longitude;
                String weatherurl = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&exclude=current,hourly,daily,alerts&appid=ced066415607aa1e214b187481c06567";
                getweather("unixt", weatherurl, new VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Gson g = new Gson();
                        threehrforecast minuterain = g.fromJson(result, threehrforecast.class);

                        LatLng point = new LatLng(lat, lon);
                        if (minuterain.minutely != null) {
                            LinkedTreeMap<String, Object> testing = (LinkedTreeMap<String, Object>) minuterain.minutely[foreMin];

                            //int transparent = (int) Math.round((Double) testing.get("precipitation"));
                            /*CircleOptions weathercircle = new CircleOptions()
                                    .center(point)
                                    .radius(span / 2.5) // In meters
                                    .fillColor(argb(transparent+10, 50, 149, 168))
                                    .strokeColor(argb(transparent+10, 50, 149, 168));
                            mMap.addCircle(weathercircle);*/
                            mMap.addMarker(new MarkerOptions()
                                    .position(point)
                                    .title("Precipitation: "+String.valueOf(testing.get("precipitation")))
                            );
                        } else {
                            //Log.i("it was ", "null");
                        }
                    }
                });


            }

            // move to location
            LatLng bikingspot = new LatLng(center.latitude, center.longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(bikingspot));
        }
        if (resultCode == 3000){
            mMap.clear();
            routepoints.clear();
        }
    }
}
