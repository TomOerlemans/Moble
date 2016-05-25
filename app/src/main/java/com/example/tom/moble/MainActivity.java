package com.example.tom.moble;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TextView topText;
    TextView bottomText;
    Button previousButton;
    int page = 0;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startscreen1);
        topText = (TextView) findViewById(R.id.topText);
        bottomText = (TextView) findViewById(R.id.bottomText);
        previousButton = (Button) findViewById(R.id.previousButton);

    }

    public void nextButtonClick(View view) {
        switch(page){
            case 0: setContentView(R.layout.startscreen2); page = 1; break;
            case 1: setContentView(R.layout.startscreen3); page = 2; break;
        }

    }

    public void previousButtonClick(View view) {
        switch(page){
            case 1: setContentView(R.layout.startscreen1); page = 0;break;
            case 2: setContentView(R.layout.startscreen2); page = 1; break;
        }
    }

    public void menuButtonClick(View view){
        setContentView(R.layout.menu);
        page = 0;
    }

    public void infoButtonClick(View view){
        setContentView(R.layout.startscreen1);
    }

    public void settingsButtonClick(View view){
        setContentView(R.layout.settings);
    }

    public void doneButtonClick(View view){
        setContentView(R.layout.menu);
    }

    public void startTimeButtonClick(View view){
        final TextView startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);

        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (hourOfDay < 12){
                            startTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minute) + " AM");
                        }else{
                            startTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minute)+ " PM");
                        }

                    }
                }, 9, 0, false);
        tpd.show();
    }



    public void endTimeButtonClick(View view){
        final TextView endTimeTextView = (TextView) findViewById(R.id.endTimeTextView);

        TimePickerDialog tpd = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (hourOfDay < 12){
                            endTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minute) + " AM");
                        }else{
                            endTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minute)+ " PM");
                        }
                    }
                }, 21, 0, false);
        tpd.show();
    }



}

