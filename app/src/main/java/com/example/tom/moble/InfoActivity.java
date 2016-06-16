package com.example.tom.moble;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class InfoActivity extends AppCompatActivity {

    Button MenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        MenuButton = (Button) findViewById(R.id.MenuButton);
        MenuButton.setBackgroundColor(Color.parseColor("#6AB344"));
    }

    public void infoMenuButtonClick(View view ){
        finish();

    }
}
