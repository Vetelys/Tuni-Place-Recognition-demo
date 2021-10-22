package com.example.tuniclassify;

import android.content.Intent;
import android.os.Bundle;
import java.util.Random;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


public class MapActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST_CODE = 1002;
    private static final int LOCATION_FAILED = 2000;

    ImageButton correctBtn;
    ImageButton falseBtn;
    ImageView location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent mapIntent = getIntent();
        float x_coord = mapIntent.getIntExtra("x_coord", 0);
        float y_coord = mapIntent.getIntExtra("y_coord", 0);
        location = findViewById(R.id.loc_marker);
        correctBtn = findViewById(R.id.correct_btn);
        falseBtn = findViewById(R.id.false_btn);

        placeCoords(x_coord, y_coord);
        //Toast.makeText(this, "Coordinates: "+ x_coord + " " + y_coord, Toast.LENGTH_LONG).show();

        correctBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", "Place was recognized successfully!");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        falseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("MESSAGE", "Place recognition failed!");
                setResult(LOCATION_FAILED, intent);
                finish();
            }
        });

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void placeCoords(float x, float y) {
        location.setY(y);
        location.setX(x);
    }
}