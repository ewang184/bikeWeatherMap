package com.example.bikeweather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class enterinfo extends AppCompatActivity{
    // declare the button
    Button thebutton;
    SeekBar sBar;
    TextView tView;
    Button resetButton;
    List<LatLng> routepoints;
    // initialize
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterinfo);
        // map button to xml button
        thebutton = findViewById(R.id.login);
        resetButton = findViewById(R.id.reset);
        sBar = (SeekBar) findViewById(R.id.simpleSeekBar);
        tView = (TextView) findViewById(R.id.textview1);
        tView.setText("Go back");
        //Intent intent=getIntent();
        //routepoints = intent.getParcelableArrayListExtra("routepoints");

        sBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int pval = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pval = progress;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(pval ==1){
                    tView.setText("Forecast "+pval + " minute ahead");
                }
                else{
                    tView.setText("Forecast " + pval + " minutes ahead");
                }
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent previousScreen = new Intent(getApplicationContext(), MapsActivity.class);
                setResult(3000, previousScreen);
                finish();
            }
        });
        thebutton.setOnClickListener(new View.OnClickListener() {
            // on click event handler
            @Override
            public void onClick(View view) {
                if(tView.getText()!="Go back") {
                    // read from xml
                    int foreMin = sBar.getProgress();

                    // send back the intent to the map with time
                    Intent previousScreen = new Intent(getApplicationContext(), MapsActivity.class);
                    previousScreen.putExtra("time", String.valueOf(foreMin));
                    setResult(1000, previousScreen);
                    finish();
                }
                else{
                    Intent previousScreen = new Intent(getApplicationContext(), MapsActivity.class);
                    setResult(2000, previousScreen);
                    finish();
                }
            }
        });
    }
}
