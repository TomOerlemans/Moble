package com.example.tom.moble;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    TextView topText;
    TextView bottomText;
    Button previousButton;
    int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topText = (TextView) findViewById(R.id.topText);
        bottomText = (TextView) findViewById(R.id.bottomText);
        previousButton = (Button) findViewById(R.id.previousButton);
    }

    public void nextButtonClick(View view){
        if(page == 0) {
            topText.setText(getResources().getString(R.string.explanation_text1));
            bottomText.setText(getResources().getString(R.string.explanation_text2));
            previousButton.setVisibility(View.VISIBLE);
            page = 1;
        }

    }

    public void previousButtonClick(View view){
        if(page == 1) {
            topText.setText(getResources().getString(R.string.welcome_text1));
            bottomText.setText(getResources().getString(R.string.welcome_text2));
            previousButton.setVisibility(View.INVISIBLE);
            page = 0;
        }

    }

}


//dit is een test comment
