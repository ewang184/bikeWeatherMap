package com.example.bikeweather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class enterinfo extends AppCompatActivity{
    // declare the button
    Button thebutton;

    // initialize
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterinfo);
        // map button to xml button
        thebutton = findViewById(R.id.login);
        thebutton.setOnClickListener(new View.OnClickListener() {
            // on click event handler
            @Override
            public void onClick(View view) {
                // read from xml editexts
                EditText timeStr = findViewById(R.id.date);
                String times = timeStr.getText().toString();
                EditText locStr = findViewById(R.id.location);
                String place = locStr.getText().toString();
                EditText radius = findViewById(R.id.radius);
                String rad = radius.getText().toString();
                EditText tfhr = findViewById(R.id.time);
                String thetime = tfhr.getText().toString();

                // try/catch for input
                String[] timeSplit = thetime.split(":",2);
                String[] dateSplit = times.split("/", 3);
                String[] placeSplit = place.split("/", 2);
                boolean pass = true;
                try{
                    int area = Integer.parseInt(rad);
                    int hour = Integer.parseInt(timeSplit[0]);
                    int minute = Integer.parseInt(timeSplit[1]);
                    int year = Integer.parseInt(dateSplit[0]);
                    int month = Integer.parseInt(dateSplit[1]);
                    int day = Integer.parseInt(dateSplit[2]);
                    float latitude = Float.parseFloat(placeSplit[0]);
                    float longitude = Float.parseFloat(placeSplit[1]);

                } catch(Exception e) {
                    pass = false;
                }
                if(pass) {
                    // send back the intent to the map with date and location
                    Intent previousScreen = new Intent(getApplicationContext(), MapsActivity.class);
                    previousScreen.putExtra("date", times);
                    previousScreen.putExtra("location", place);
                    previousScreen.putExtra("radius", rad);
                    previousScreen.putExtra("time", thetime);
                    setResult(1000, previousScreen);
                    finish();
                }

            }
        });
    }
}
