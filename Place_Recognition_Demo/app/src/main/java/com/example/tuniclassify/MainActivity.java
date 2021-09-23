package com.example.tuniclassify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String URL = "192.168.1.2";
    private static final String PORT = "5000";
    private static final String SERVER_URL = "http://192.168.1.2:5000/";

    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int LOCATION_REQUEST_CODE = 1002;

    boolean picTaken = false;

    ImageButton cameraBtn;
    ImageButton uploadBtn;
    ImageButton serverBtn;
    ImageView cameraView;
    Uri img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.image_view);
        cameraBtn = findViewById(R.id.camera_btn);
        uploadBtn = findViewById(R.id.upload_btn);
        serverBtn = findViewById(R.id.server_btn);

        // camera button clicked
        cameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //check for camera and external storage write permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.INTERNET )==
                            PackageManager.PERMISSION_DENIED){
                        // permission denied, request permission
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
                        requestPermissions(permission, PERMISSION_REQUEST_CODE);
                    }
                    else{
                        //permissions OK
                        getPicture();
                    }
                }
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!picTaken){
                    Toast.makeText(MainActivity.this, "Take a picture before uploading it", Toast.LENGTH_SHORT).show();
                }
                else{
                    openMapActivity();
                }

            }
        });
        serverBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(MainActivity.this, ServerActivity.class);
                startActivity(settingsIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }



    public void openMapActivity() {
        Intent mapIntent = new Intent(this, MapActivity.class);
        //TODO: retrieve coords from server
        Random random = new Random();
        int max = 500;
        int min = 5;
        int x = random.nextInt((max-min) +1) + min;
        int y = random.nextInt((max-min) +1) + min;;
        mapIntent.putExtra("x_coord", x);
        mapIntent.putExtra("y_coord", y);
        startActivityForResult(mapIntent, LOCATION_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void getPicture() {
        ContentValues values = new ContentValues();
        //timestamp as image name
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String dateNow = date.format(new Date());
        Toast.makeText(this, "Opening camera", Toast.LENGTH_SHORT).show();
        values.put(MediaStore.Images.Media.TITLE, dateNow);
        values.put(MediaStore.Images.Media.DESCRIPTION, "TuniClassify image");
        img = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, img);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

    }

    //called when denying permissions on popup
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getPicture();
                }
                else{
                    //permission denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                cameraView.setImageURI(img);
                picTaken = true;
            }
        }
        else if(requestCode == LOCATION_REQUEST_CODE){
            {
                if(resultCode == RESULT_OK){
                    Toast.makeText(this, "Place was recognized successfully!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(this, "Place recognition failed!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}