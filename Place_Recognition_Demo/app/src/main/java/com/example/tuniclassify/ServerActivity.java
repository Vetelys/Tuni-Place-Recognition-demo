package com.example.tuniclassify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.SearchEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ServerActivity extends AppCompatActivity {

    private static final int SETTINGS_CHANGED = 2001;
    EditText portView;
    EditText ipAddressView;
    Button settingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        settingButton = findViewById(R.id.settingButton);
        portView = findViewById(R.id.portNumber);
        ipAddressView = findViewById(R.id.IPAddress);

        settingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveSettings(view);
            }
        });
    }
    void saveSettings(View v){
        Intent settingIntent = new Intent(this, MainActivity.class);
        String port = portView.getText().toString();
        String ip = ipAddressView.getText().toString();
        settingIntent.putExtra("ip", ip);
        settingIntent.putExtra("port", port);
        setResult(RESULT_OK, settingIntent);
        finish();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}