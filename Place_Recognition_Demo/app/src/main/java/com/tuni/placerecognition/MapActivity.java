package com.tuni.placerecognition;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;


public class MapActivity extends AppCompatActivity {

    private static final int LOCATION_FAILED = 2000;

    ImageButton correctBtn;
    ImageButton falseBtn;
    ImageView mapView;
    TextView coordinateView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        coordinateView = findViewById(R.id.coordinateTextView);
        mapView = findViewById(R.id.map_view);
        correctBtn = findViewById(R.id.correct_btn);
        falseBtn = findViewById(R.id.false_btn);

        //path for saved map image
        Bitmap map_img;
        String img_path = getIntent().getStringExtra("img_path");
        String coords = getIntent().getStringExtra("coordinates");
        try{
            coordinateView.setText(coords);
            FileInputStream is = this.openFileInput(img_path);
            map_img = BitmapFactory.decodeStream(is);
            mapView.setImageBitmap(map_img);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


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
}
