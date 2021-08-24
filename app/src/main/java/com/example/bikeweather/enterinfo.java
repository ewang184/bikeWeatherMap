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

public class enterinfo extends AppCompatActivity{
    // declare the button
    Button thebutton;
    SeekBar sBar;
    TextView tView;
    // initialize
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterinfo);
        // map button to xml button
        thebutton = findViewById(R.id.login);
        sBar = (SeekBar) findViewById(R.id.simpleSeekBar);
        tView = (TextView) findViewById(R.id.textview1);
        tView.setText("Forecast - minutes ahead");
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

        thebutton.setOnClickListener(new View.OnClickListener() {
            // on click event handler
            @Override
            public void onClick(View view) {
                // read from xml editexts

                EditText locStr = findViewById(R.id.location);
                String place = locStr.getText().toString();
                EditText radius = findViewById(R.id.radius);
                String rad = radius.getText().toString();
                int foreMin = sBar.getProgress();
                Log.i("try bar", String.valueOf(foreMin));
                // try/catch for input

                String[] placeSplit = place.split("/", 2);
                boolean pass = true;
                try{
                    int area = Integer.parseInt(rad);
                    float latitude = Float.parseFloat(placeSplit[0]);
                    float longitude = Float.parseFloat(placeSplit[1]);

                } catch(Exception e) {
                    pass = false;
                }
                if(pass) {
                    // send back the intent to the map with date and location
                    Intent previousScreen = new Intent(getApplicationContext(), MapsActivity.class);
                    Log.i("try bar", String.valueOf(foreMin));
                    previousScreen.putExtra("location", place);
                    previousScreen.putExtra("radius", rad);
                    previousScreen.putExtra("time", String.valueOf(foreMin));
                    setResult(1000, previousScreen);
                    finish();
                }

            }
        });
    }
}
