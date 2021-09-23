package com.example.tuniclassify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

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

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 1001;

    ImageButton cameraBtn;
    ImageButton uploadBtn;
    ImageView cameraView;
    Uri img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.image_view);
        cameraBtn = findViewById(R.id.camera_btn);
        uploadBtn = findViewById(R.id.upload_btn);

        // camera button clicked
        cameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //check for camera and external storage write permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                        // permission denied, request permission
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_REQUEST_CODE);
                    }
                    else{
                        //permissions OK
                        getPicture();
                    }
                }
            }
        });

    }

    private void getPicture() {
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

    //camera activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            cameraView.setImageURI(img);
        }
    }
}