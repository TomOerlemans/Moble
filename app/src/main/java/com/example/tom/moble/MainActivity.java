package com.example.tom.moble;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    int firstLaunch;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        firstLaunch = sharedPref.getInt("First Launch", 0);
        if(firstLaunch == 0) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("First Launch", 1);
            editor.commit();
            setContentView(R.layout.startscreen1);
        }else{
            setContentView(R.layout.menu);
        }



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

    public void entryTestButtonClick(View view){
        setContentView(R.layout.quiz_layout);
    }

    public void quizAnswerButtonClick(View view){

        switch(view.getId()){
            case R.id.multipleChoiceAnswer1Button:
                //String a  = findViewById(R.color.stdButton).toString();
                //  getResources().getColor(R.color.stdButton);
                // view.getBackground().setColorFilter(getResources().getColor(R.color.wrongAnswer), android.graphics.PorterDuff.Mode.MULTIPLY);
                //((ImageView) view).setColorFilter(getResources().getColor(R.color.rightAnswer), android.graphics.PorterDuff.Mode.MULTIPLY);
                //findViewById(R.id.multipleChoiceAnswer1Button).setFi(getResources().getColor(R.color.rightAnswer));
                break;
            case R.id.multipleChoiceAnswer2Button:
                findViewById(R.id.multipleChoiceAnswer2Button).setBackgroundColor(getResources().getColor(R.color.rightAnswer));
                break;
            case R.id.multipleChoiceAnswer3Button:
                findViewById(R.id.multipleChoiceAnswer3Button).setBackgroundColor(getResources().getColor(R.color.rightAnswer));
                break;
            case R.id.multipleChoiceAnswer4Button:
                findViewById(R.id.multipleChoiceAnswer4Button).setBackgroundColor(getResources().getColor(R.color.rightAnswer));
                break;
            case R.id.multipleChoiceAnswer5Button:
                findViewById(R.id.multipleChoiceAnswer5Button).setBackgroundColor(getResources().getColor(R.color.rightAnswer));
                break;
            case R.id.multipleChoiceAnswer6Button:
                findViewById(R.id.multipleChoiceAnswer6Button).setBackgroundColor(getResources().getColor(R.color.rightAnswer));
                break;
            default:
                break;
        }

        //Button b = (Button)view;
        //String givenAns = b.getText().toString();
        //String buttonId = b.;

        //v.setVisibility(View.VISIBLE);
    }

}



