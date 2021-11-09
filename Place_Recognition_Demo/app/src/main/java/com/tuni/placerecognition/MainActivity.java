package com.tuni.placerecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    //default server ip and port
    private static String URL;
    private static String RESPONSE_TYPE;

    private final OkHttpClient client = new OkHttpClient();

    private static String currentPhotoPath;
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");

    private static final int PERMISSION_REQUEST_CODE = 1000;
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int LOCATION_REQUEST_CODE = 1002;
    private static final int SETTINGS_REQUEST_CODE = 1003;

    private static Uri img = null;
    boolean picTaken = false;

    ImageButton cameraBtn;
    ImageButton uploadBtn;
    ImageButton serverBtn;
    ImageView cameraView;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.image_view);
        cameraBtn = findViewById(R.id.camera_btn);
        uploadBtn = findViewById(R.id.upload_btn);
        serverBtn = findViewById(R.id.server_btn);

        //fixes NetworkOnMainThreadException crash that using threads wouldn't fix
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        RESPONSE_TYPE = sharedPreferences.getString("textResponse", "image");
        URL = sharedPreferences.getString("url", "http://192.168.1.2:5000/");


        // camera button clicked
        cameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //check for camera and storage write permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.INTERNET )==
                            PackageManager.PERMISSION_DENIED){
                        // permission denied, request permission
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
                        requestPermissions(permission, PERMISSION_REQUEST_CODE);
                    }
                    else{
                        //permissions OK
                        getPicture();
                    }
                }
            }
        });

        //upload image button clicked
        uploadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (!picTaken) {
                    Toast.makeText(MainActivity.this, "Take a picture before uploading it", Toast.LENGTH_SHORT).show();
                } else {
                    //networking in its own thread
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                uploadImage();
                            }catch(Exception e){
                                e.printStackTrace();
                                runOnUiThread(()-> Toast.makeText(MainActivity.this, "Could not connect to server! Check server settings.", Toast.LENGTH_LONG).show());
                            }
                        }
                    });
                    thread.start();
                }
            }
        });

        serverBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                openSettingsActivity();
            }
        });
    }


    //FROM HERE: https://developer.android.com/training/camera/photobasics
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void setResult(String result) {
        File file = new File(currentPhotoPath);

        String filename = file.getName();

        RequestBody req = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image_name", filename)
                .addFormDataPart("result", result)
                .build();

        Request request = new Request.Builder()
                .url(URL+"result")
                .post(req)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Could not save result!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Estimation result set as: " + result, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }

    private void uploadImage() {

        File file = new File(currentPhotoPath);

        String filename = file.getName();

        Log.d("name", filename);

        RequestBody req = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", filename,
                RequestBody.create(MEDIA_TYPE_JPG, file))
                .build();

        Request request = new Request.Builder()
                .url(URL+"upload/"+RESPONSE_TYPE)
                .post(req)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Logger logger = Logger.getAnonymousLogger();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "File upload failed! Check server settings.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(response.code() != 200){
                                Toast.makeText(MainActivity.this, "Could not get coordinates for given picture!", Toast.LENGTH_LONG).show();
                            }else{
                                Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
                                if(RESPONSE_TYPE.equals("image")){
                                    String img_name = "map.png";
                                    InputStream inputStream = response.body().byteStream();
                                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                    inputStream.close();

                                    FileOutputStream outputStream = openFileOutput(img_name, Context.MODE_PRIVATE);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                                    //cleanup
                                    outputStream.close();
                                    bitmap.recycle();

                                    mapIntent.putExtra("img_path", img_name);

                                }else{
                                    String output = response.body().string();
                                    Toast.makeText(MainActivity.this, output, Toast.LENGTH_LONG).show();
                                    mapIntent.putExtra("coordinates", output);
                                }
                                startActivityForResult(mapIntent, LOCATION_REQUEST_CODE);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                            }

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Could not upload image", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }



    public void openSettingsActivity(){
        Intent settingsIntent = new Intent(this, ServerActivity.class);
        startActivityForResult(settingsIntent, SETTINGS_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    public void getPicture() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }   catch (IOException ex){
                Toast.makeText(this, "Could not create image file!", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                img = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, img);

                Toast.makeText(this, "Opening camera", Toast.LENGTH_SHORT).show();
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        }
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
                Log.d("current path", currentPhotoPath);
            }
        }
        else if(requestCode == LOCATION_REQUEST_CODE){
            {
                if(resultCode == RESULT_OK){
                    setResult("Correct");
                }
                else{
                    setResult("False");
                }
            }

        }else if(requestCode == SETTINGS_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                URL = sharedPreferences.getString("url", "http://192.168.1.2:5000/");
                RESPONSE_TYPE = sharedPreferences.getString("responseType", "image");
            }else{
                Toast.makeText(this, "Could not save settings.",Toast.LENGTH_LONG).show();
            }
        }
    }
}