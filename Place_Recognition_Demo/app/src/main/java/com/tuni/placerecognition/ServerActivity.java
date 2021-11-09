package com.tuni.placerecognition;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class ServerActivity extends AppCompatActivity {
    private static final int SETTINGS_REQUEST_CODE = 1003;
    EditText portView;
    EditText ipAddressView;
    EditText urlAddressView;
    Button settingButton;
    TextView currentUrlView;
    Switch responseTypeCheck;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        currentUrlView = findViewById(R.id.currentUrlView);
        responseTypeCheck = findViewById(R.id.responseSwitch);
        settingButton = findViewById(R.id.settingButton);
        portView = findViewById(R.id.portNumber);
        ipAddressView = findViewById(R.id.IPAddress);
        urlAddressView = findViewById(R.id.urlAddress);
        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String response_type = sharedPreferences.getString("responseType", "image");
        responseTypeCheck.setChecked(!response_type.equals("image"));
        String currentUrl = "Current URL: " + sharedPreferences.getString("url", "");
        currentUrlView.setText(currentUrl);


        settingButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                saveSettings(view);
            }
        });
    }
    public void saveSettings(View view){
        String urlToUse = null;
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        Intent settingIntent = new Intent(this, MainActivity.class);
        String port = portView.getText().toString();
        String ip = ipAddressView.getText().toString();
        String url = urlAddressView.getText().toString();
        
        if(url.equals("") && !ip.equals("") && !port.equals("")){
            urlToUse = "http://"+ip+":"+port+"/";
        }
        else if(!url.equals("")){
            urlToUse = url;
        }
        if(urlToUse != null){
            myEdit.putString("url", urlToUse);
        }

        if(responseTypeCheck.isChecked()) {
            myEdit.putString("responseType", "text");
        }else{
            myEdit.putString("responseType", "image");
        }
        myEdit.commit();
        setResult(RESULT_OK, settingIntent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}